package dao;

import entity.Document;
import entity.ShingleBuilder;
import entity.ShingleDTO;
import exception.DBException;
import exception.ServerException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ShingleManager {

    private static final String SQL_FIND_SHINGLES = "Select * from shingles where docId=? and shingleSize=?";
    private static final String SQL_CREATE_SHINGLE = "INSERT into shingles (docId,shingleHashCode,shingleSize) values (?,?,?)";
    private static final String SQL_CREATE_DOC = "INSERT into docs(path, wordsCount) values (?,?)";
    private static final String SQL_GET_DOCS = "Select * from docs";
    private static final String SQL_GET_DOC_BY_PATH = "Select * from docs where path = ?";
    private static ShingleManager instance;

    DataSource ds;

    /**
     * singleton realisation of accelerator(DAO) for Shingle and docs.
     */
    public static synchronized ShingleManager getInstance() {
        if (instance == null) {
            instance = new ShingleManager();
        }
        return instance;
    }

    private ShingleManager() {

    }

    public synchronized List<ShingleDTO> getShinglesForDocId(int docId, int shingleSize) throws DBException, SQLException, ServerException {
        Connection con = DBUtils.getСonn(ds);
        List<ShingleDTO> shingleDTOS = new ArrayList<>();

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = con.prepareStatement(SQL_FIND_SHINGLES);
            int k = 0;
            stmt.setInt(++k, docId);
            stmt.setInt(++k, shingleSize);
            rs = stmt.executeQuery();
            while (rs.next()) {
                shingleDTOS.add(extractShingle(rs));
            }
        } catch (SQLException e) {
            throw new DBException(Messages.ERR_CANNOT_OBTAIN_SHINGLE_LIST, e);
        } finally {
            DBUtils.close(rs);
            DBUtils.close(stmt);
            DBUtils.close(con);
        }
        return shingleDTOS;
    }

    public synchronized boolean serializeDocument(List<Integer> shingles, int docId, int shingleLen) throws ServerException, DBException, SQLException {
        Connection con = DBUtils.getСonn(ds);
        PreparedStatement pstmt = null;
        try {
            for (Integer code : shingles) {
                pstmt = con.prepareStatement(SQL_CREATE_SHINGLE, Statement.RETURN_GENERATED_KEYS);
                int k = 0;
                pstmt.setInt(++k, docId);
                pstmt.setInt(++k, code);
                pstmt.setInt(++k, shingleLen);
                if (!(pstmt.executeUpdate() > 0)) {
                    return false;
                }
            }
            con.commit();
        } catch (SQLException e) {
            throw new DBException(Messages.ERR_CANNOT_CREATE_SHINGLE, e);
        } finally {
            DBUtils.close(pstmt);
            DBUtils.close(con);
        }
        return true;
    }

    public synchronized boolean createDoc(String path, int wordsCount) throws DBException, SQLException, ServerException {
        Connection con = DBUtils.getСonn(ds);
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(SQL_CREATE_DOC, Statement.RETURN_GENERATED_KEYS);
            int k = 0;
            pstmt.setString(++k, path);
            pstmt.setInt(++k, wordsCount);
            if (pstmt.executeUpdate() > 0) {
                con.commit();
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            DBUtils.close(pstmt);
            DBUtils.close(con);
        }
        return false;
    }

    public synchronized List<Document> getDocumentList() throws DBException, SQLException, ServerException {
        Connection con = DBUtils.getСonn(ds);
        List<Document> documents = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement(SQL_GET_DOCS);
            rs = stmt.executeQuery();
            while (rs.next()) {
                documents.add(extractDoc(rs));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            DBUtils.close(stmt);
            DBUtils.close(con);
            DBUtils.close(rs);
        }
        return documents;
    }
    public synchronized Document getDocumentByPath(String path) throws DBException, SQLException, ServerException {
        Connection con = DBUtils.getСonn(ds);
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement(SQL_GET_DOC_BY_PATH);
            int k=0;
            stmt.setString(++k,path);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return extractDoc(rs);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            DBUtils.close(stmt);
            DBUtils.close(con);
            DBUtils.close(rs);
        }
        return new Document();
    }

    private Document extractDoc(ResultSet rs) throws SQLException {
        Document doc = new Document();
        doc.setId(rs.getInt("id"));
        doc.setPath(rs.getString("path"));
        doc.setWordsCount(rs.getInt("wordsCount"));
        return doc;
    }

    private synchronized ShingleDTO extractShingle(ResultSet rs) throws SQLException {
        return new ShingleBuilder().setDocumentId(rs.getInt("docId")).setShingleSize(rs.getInt("shingleSize")).setShingleText(rs.getInt("shingleHashCode")).get();
    }
}
