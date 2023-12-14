package petsafe;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SQLite {
  private static Connection conn = null;
  private static PreparedStatement pstmt = null;
  private static Statement stmt = null;

  private void connect() {
    try {
      if (conn == null) {
        String url;

        if (App.runningFromJAR() || App.runningFromEXE()) {
          String jarPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
          String jarDir = new File(jarPath).getParent().replace("\\target", "").replace("%20", " ");
          url = "jdbc:sqlite:" + jarDir + File.separator + "petsafe.db";

        } else {
          url = "jdbc:sqlite:petsafe.db";
        }

        System.out.println("Running from JAR: " + App.runningFromJAR());
        System.out.println("Database URL: " + url);
        
        try {
          Class.forName("org.sqlite.JDBC");
          conn = DriverManager.getConnection(url);
          conn.setAutoCommit(true);

        } catch (ClassNotFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

        System.out.println("Connection to sqlite has been established");

      } else {
        if (pstmt != null) pstmt.close();
        if (stmt != null) stmt.close();
        conn.close();

        pstmt = null;
        stmt = null;
        conn = null;

        System.out.println("Database Connection closed");
        connect();
      }
    } catch (SQLException e) {
      // TODO: handle exception
      System.out.println(e.getMessage());
    }
  }

  public ResultSet selectAll(String query) {
    ResultSet rs = null;

    try {
      connect();

      if (conn == null) {
        throw new SQLException("Database connection error");
      }

      stmt = conn.createStatement();
      rs = stmt.executeQuery(query);

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    return rs;
  }

  public boolean addRow(String table, String[] columns, String[] values) {
    if (columns.length == values.length) {
      int arrayLength = values.length;

      List<String> questionMarks = new ArrayList<>();
      for (int i = 0; i < arrayLength; i++) {
        questionMarks.add("?");
      }

      String sql = "INSERT INTO " + table + "( " + String.join(", ", columns) + ") VALUES ("
          + String.join(", ", questionMarks) + ")";

      connect();

      try {
        if (conn == null) {
          throw new SQLException("Database connection error");
        }
        pstmt = conn.prepareStatement(sql);

        for (int i = 1; i <= arrayLength; i++) {
          pstmt.setString(i, values[i - 1]);
        }
        pstmt.executeUpdate();
        return true;

      } catch (SQLException e) {
        // TODO: handle exception
        System.out.println(e.getMessage());
      }

    } else {
      throw new ArrayIndexOutOfBoundsException("Columns and Values are unequal in length");
    }

    return false;
  }

  
}
