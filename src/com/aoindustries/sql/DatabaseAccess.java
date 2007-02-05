package com.aoindustries.sql;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.IntList;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Wraps and simplifies access to a JDBC database.
 *
 * @author  AO Industries, Inc.
 */
public interface DatabaseAccess {

    /**
     * Read-only query the database with a <code>BigDecimal</code> return type.
     * <ul>
     *   <li>isolationLevel = <code>Connection.TRANSACTION_READ_COMMITTED</code></li>
     *   <li>readOnly = <code>true</code></li>
     *   <li>rowRequired = <code>true</code></li>
     * </ul>
     */
    BigDecimal executeBigDecimalQuery(String sql, Object ... params) throws IOException, SQLException;

    /**
     * Query the database with a <code>BigDecimal</code> return type.
     */
    BigDecimal executeBigDecimalQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException;

    /**
     * Read-only query the database with a <code>boolean</code> return type.
     * <ul>
     *   <li>isolationLevel = <code>Connection.TRANSACTION_READ_COMMITTED</code></li>
     *   <li>readOnly = <code>true</code></li>
     *   <li>rowRequired = <code>true</code></li>
     * </ul>
     */
    boolean executeBooleanQuery(String sql, Object ... params) throws IOException, SQLException;

    /**
     * Query the database with a <code>boolean</code> return type.
     */
    boolean executeBooleanQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException;
    
    /**
     * Read-only query the database with a <code>java.sql.Date</code> return type.
     * <ul>
     *   <li>isolationLevel = <code>Connection.TRANSACTION_READ_COMMITTED</code></li>
     *   <li>readOnly = <code>true</code></li>
     *   <li>rowRequired = <code>true</code></li>
     * </ul>
     */
    Date executeDateQuery(String sql, Object ... params) throws IOException, SQLException;

    /**
     * Query the database with a <code>java.sql.Date</code> return type.
     */
    Date executeDateQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException;

    /**
     * Read-only query the database with a <code>IntList</code> return type.
     * <ul>
     *   <li>isolationLevel = <code>Connection.TRANSACTION_READ_COMMITTED</code></li>
     *   <li>readOnly = <code>true</code></li>
     *   <li>rowRequired = <code>true</code></li>
     * </ul>
     */
    IntList executeIntListQuery(String sql, Object ... params) throws IOException, SQLException;

    /**
     * Query the database with a <code>IntList</code> return type.
     */
    IntList executeIntListQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws IOException, SQLException;

    /**
     * Read-only query the database with a <code>int</code> return type.
     * <ul>
     *   <li>isolationLevel = <code>Connection.TRANSACTION_READ_COMMITTED</code></li>
     *   <li>readOnly = <code>true</code></li>
     *   <li>rowRequired = <code>true</code></li>
     * </ul>
     */
    int executeIntQuery(String sql, Object ... params) throws IOException, SQLException;

    /**
     * Query the database with a <code>int</code> return type.
     */
    int executeIntQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException;

    /**
     * Read-only query the database with a <code>long</code> return type.
     * <ul>
     *   <li>isolationLevel = <code>Connection.TRANSACTION_READ_COMMITTED</code></li>
     *   <li>readOnly = <code>true</code></li>
     *   <li>rowRequired = <code>true</code></li>
     * </ul>
     */
    long executeLongQuery(String sql, Object ... params) throws IOException, SQLException;

    /**
     * Query the database with a <code>long</code> return type.
     */
    long executeLongQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException;

    /**
     * Read-only query the database with a <code>&lt;T&gt;</code> return type.  Class &lt;T&gt; must have a contructor that takes a single argument of <code>ResultSet</code>.
     * <ul>
     *   <li>isolationLevel = <code>Connection.TRANSACTION_READ_COMMITTED</code></li>
     *   <li>readOnly = <code>true</code></li>
     *   <li>rowRequired = <code>true</code></li>
     * </ul>
     */
    <T> T executeObjectQuery(Class<T> clazz, String sql, Object ... params) throws IOException, SQLException;

    /**
     * Query the database with a <code>&lt;T&gt;</code> return type.  Class &lt;T&gt; must have a contructor that takes a single argument of <code>ResultSet</code>.
     */
    <T> T executeObjectQuery(int isolationLevel, boolean readOnly, boolean rowRequired, Class<T> clazz, String sql, Object ... params) throws IOException, SQLException;

    /**
     * Read-only query the database with a <code>List&lt;T&gt;</code> return type.  Class &lt;T&gt; must have a contructor that takes a single argument of <code>ResultSet</code>.
     * <ul>
     *   <li>isolationLevel = <code>Connection.TRANSACTION_READ_COMMITTED</code></li>
     *   <li>readOnly = <code>true</code></li>
     *   <li>rowRequired = <code>true</code></li>
     * </ul>
     */
    <T> List<T> executeObjectListQuery(Class<T> clazz, String sql, Object ... params) throws IOException, SQLException;

    /**
     * Query the database with a <code>List&lt;T&gt;</code> return type.  Class &lt;T&gt; must have a contructor that takes a single argument of <code>ResultSet</code>.
     */
    <T> List<T> executeObjectListQuery(int isolationLevel, boolean readOnly, Class<T> clazz, String sql, Object ... params) throws IOException, SQLException;

    /**
     * Read-only query the database with a <code>short</code> return type.
     * <ul>
     *   <li>isolationLevel = <code>Connection.TRANSACTION_READ_COMMITTED</code></li>
     *   <li>readOnly = <code>true</code></li>
     *   <li>rowRequired = <code>true</code></li>
     * </ul>
     */
    short executeShortQuery(String sql, Object ... params) throws IOException, SQLException;

    /**
     * Query the database with a <code>short</code> return type.
     */
    short executeShortQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException;

    /**
     * Read-only query the database with a <code>String</code> return type.
     * <ul>
     *   <li>isolationLevel = <code>Connection.TRANSACTION_READ_COMMITTED</code></li>
     *   <li>readOnly = <code>true</code></li>
     *   <li>rowRequired = <code>true</code></li>
     * </ul>
     */
    String executeStringQuery(String sql, Object ... params) throws IOException, SQLException;

    /**
     * Query the database with a <code>String</code> return type.
     */
    String executeStringQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException;

    /**
     * Read-only query the database with a <code>List&lt;String&gt;</code> return type.
     * <ul>
     *   <li>isolationLevel = <code>Connection.TRANSACTION_READ_COMMITTED</code></li>
     *   <li>readOnly = <code>true</code></li>
     *   <li>rowRequired = <code>true</code></li>
     * </ul>
     */
    List<String> executeStringListQuery(String sql, Object ... params) throws IOException, SQLException;

    /**
     * Query the database with a <code>List&lt;String&gt;</code> return type.
     */
    List<String> executeStringListQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws IOException, SQLException;

    /**
     * Read-only query the database with a <code>Timestamp</code> return type.
     * <ul>
     *   <li>isolationLevel = <code>Connection.TRANSACTION_READ_COMMITTED</code></li>
     *   <li>readOnly = <code>true</code></li>
     *   <li>rowRequired = <code>true</code></li>
     * </ul>
     */
    Timestamp executeTimestampQuery(String sql, Object ... params) throws IOException, SQLException;

    /**
     * Query the database with a <code>Timestamp</code> return type.
     */
    Timestamp executeTimestampQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException;

    /**
     * Performs an update on the database and returns the number of rows affected.
     */
    int executeUpdate(String sql, Object ... params) throws IOException, SQLException;
}
