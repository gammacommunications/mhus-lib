package de.mhus.lib.core.console;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.mhus.lib.core.MString;

public class ConsoleTable {
    
	public static final String SEPARATOR_LINE = "---";
    
	public List<String> header = new ArrayList<String>();
    public List<List<String[]>> content = new ArrayList<>();
    private int maxColSize = -1;
    private boolean lineSpacer = false;
    private boolean multiLine = true;
    
    public Row addRow() {
        return new Row(addIntRow());
    }
    
    private List<String[]> addIntRow() {
        List<String[]> row = new ArrayList<>();
        content.add(row);
        return row;
    }
    
    public void addRowValues(Object ... values) {
    	List<String[]> row = addIntRow();
    	for (Object v : values) {
    		if (v == null)
    			row.add(new String[] {""});
    		else
    			row.add(String.valueOf(v).split("\n"));
    	}
    }
    
    public List<String> getHeader() {
    	return header;
    }
    
    public void setHeaderValues(String ... values) {
    	List<String> row = getHeader();
    	row.clear();
    	for (String v : values)
    		row.add(v);
    }
    
    public void print(Console console)  {
    	setMaxColSize(console.getWidth());
    	print((PrintStream)console);
    }
    
    public void print(PrintStream out)  {
        int[] sizes = new int[header.size()];
        updateHeaderSizes(sizes, header);
        for (List<String[]> row : content) {
            updateSizes(sizes, row);
        }
        String headerLine = getHeaderRow(sizes, header, " | ");
        out.println(headerLine);
        out.println(underline(headerLine.length()));
        boolean first = true;
        for (List<String[]> row : content) {
        	if (!first && lineSpacer) out.println();
        	int rowHeight = getRowHeight(row);
        	for (int l = 0; l < rowHeight; l++)
        		out.println(getRow(sizes, row, " | ", l));
            first = false;
        }
    }

    public void print(PrintWriter out)  {
        int[] sizes = new int[header.size()];
        updateSizes(sizes);
        String headerLine = getHeaderRow(sizes, header, " | ");
        out.println(headerLine);
        out.println(underline(headerLine.length()));
        boolean first = true;
        for (List<String[]> row : content) {
        	if (!first && lineSpacer) out.println();
        	int rowHeight = getRowHeight(row);
        	for (int l = 0; l < rowHeight; l++)
        		out.println(getRow(sizes, row, " | ", l));
            first = false;
        }
    }
    
    private int getRowHeight(List<String[]> row) {
    	if (!multiLine) return 1;
    	int height = 1;
    	for (String[] cell : row)
    		height = Math.max(height, cell.length);
		return height;
	}

	private String underline(int length) {
        char[] exmarks = new char[length];
        Arrays.fill(exmarks, '-');
        return new String(exmarks);
    }

    private String getRow(int[] sizes, List<String[]> row, String separator, int cellLine) {
    	
    	if (row.size() == 1 && row.get(0).equals(SEPARATOR_LINE)) {
    		int s = 0;
    		for (int i : sizes) {
    			if (s != 0) s+=separator.length();
    			s+=i;
    		}
    		return MString.rep('-', s );
    	}
    	
        StringBuilder line = new StringBuilder();
        int c = 0;
        for (String[] cells : row) {
        	String cell = getCellLine(cells,cellLine);
            if (maxColSize > 0 && cell.length() > maxColSize) {
				cell = MString.truncateNice(cell, maxColSize);
            }
            cell = cell.replaceAll("\n", "");
            line.append(String.format("%-" + sizes[c] + "s", cell));
            if (c + 1 < row.size()) {
                line.append(separator);
            }
            c++;
        }
        return line.toString();
    }

    private String getHeaderRow(int[] sizes, List<String> row, String separator) {
    	
    	if (row.size() == 1 && row.get(0).equals(SEPARATOR_LINE)) {
    		int s = 0;
    		for (int i : sizes) {
    			if (s != 0) s+=separator.length();
    			s+=i;
    		}
    		return MString.rep('-', s );
    	}
    	
        StringBuilder line = new StringBuilder();
        int c = 0;
        for (String cell : row) {
            if (cell == null) {
                cell = "";
            }
            if (maxColSize > 0 && cell.length() > maxColSize) {
				cell = MString.truncateNice(cell, maxColSize);
            }
            cell = cell.replaceAll("\n", "");
            line.append(String.format("%-" + sizes[c] + "s", cell));
            if (c + 1 < row.size()) {
                line.append(separator);
            }
            c++;
        }
        return line.toString();
    }

    private String getCellLine(String[] cells, int line) {
    	if (!multiLine) {
    		if (line == 0) {
    			return MString.join(cells, ' ');
    		} else
    			return "";
    	}
    	if (cells == null || line < 0 || line >= cells.length) return "";
		return cells[line];
	}

	private void updateSizes(int[] sizes) {
        updateHeaderSizes(sizes, header);
        for (List<String[]> row : content) {
            updateSizes(sizes, row);
        }
    }
    
    private void updateSizes(int[] sizes, List<String[]> row) {
        int c = 0;
        for (String[] cells : row) {
        	for (String cellContent : cells) {
	            int cellSize = cellContent != null ? cellContent.length() : 0;
	            cellSize = maxColSize > 0 ? Math.min(cellSize, maxColSize) : cellSize;
	            if (cellSize > sizes[c]) {
	                sizes[c] = cellSize;
	            }
        	}
        	c++;
        }
    }

    private void updateHeaderSizes(int[] sizes, List<String> row) {
        int c = 0;
        for (String cellContent : row) {
            int cellSize = cellContent != null ? cellContent.length() : 0;
            cellSize = maxColSize > 0 ? Math.min(cellSize, maxColSize) : cellSize;
            if (cellSize > sizes[c]) {
                sizes[c] = cellSize;
            }
            c++;
        }
    }
    
	public int getMaxColSize() {
		return maxColSize;
	}

	public void setMaxColSize(int maxColSize) {
		this.maxColSize = maxColSize;
	}

	public boolean isLineSpacer() {
		return lineSpacer;
	}

	public void setLineSpacer(boolean lineSpacer) {
		this.lineSpacer = lineSpacer;
	}
	
	@Override
	public String toString() {
		
		StringWriter sw = new StringWriter();
		PrintWriter ps = new PrintWriter(sw);
		print(ps);
		
		return sw.toString();
	}
	
	public static ConsoleTable fromJdbcResult(ResultSet res) throws SQLException {
		ResultSetMetaData resMeta = res.getMetaData();
		ConsoleTable out = new ConsoleTable();
		String[] h = new String[resMeta.getColumnCount()];
		for (int i = 0; i < resMeta.getColumnCount(); i++)
			h[i] = resMeta.getColumnName(i+1);
		
		out.setHeaderValues(h);
		while (res.next()) {
			List<String[]> r = out.addIntRow();
			for (int i = 0; i < resMeta.getColumnCount(); i++)
				r.add(String.valueOf(res.getObject(i+1)).split("\n") );
		}
		return out;
	}

	public String[] toStringArray(boolean showHeader) {
		int i = showHeader ? 1 : 0;
		String[] out = new String[content.size() + i];
        int[] sizes = new int[header.size()];
        updateSizes(sizes);
        if (showHeader)
        	out[0] = getHeaderRow(sizes, header, " | ");
        for (List<String[]> row : content) {
        	int rowHeight = getRowHeight(row);
        	for (int l = 0; l < rowHeight; l++)
        		out[i] = getRow(sizes, row, " | ", l);
            i++;
        }
		return out;
	}

	public String[][] toStringMatrix(boolean showHeader) {
		int i = showHeader ? 1 : 0;
		String[][] out = new String[content.size() + i][];
        if (showHeader)
        	out[0] = getHeaderRowArray(header);
        for (List<String[]> row : content) {
        	int rowHeight = getRowHeight(row);
        	for (int l = 0; l < rowHeight; l++)
        		out[i] = getRowArray(row, l);
            i++;
        }
		return out;
	}

	private String[] getRowArray(List<String[]> row, int line) {
		String[] out = new String[row.size()];
		for (int i = 0; i < row.size(); i++)
			out[i] = getCellLine(row.get(i), line);
		return out;
	}
	
	private String[] getHeaderRowArray(List<String> row) {
		return row.toArray(new String[row.size()]);
	}
	
	public boolean isMultiLine() {
		return multiLine;
	}

	public void setMultiLine(boolean multiLine) {
		this.multiLine = multiLine;
	}

	public static class Row {

		private List<String[]> row;

		public Row(List<String[]> row) {
			this.row = row;
		}

		public void add(String v) {
    		if (v == null)
    			row.add(new String[] {""});
    		else
    			row.add(String.valueOf(v).split("\n"));
		}
	}

}