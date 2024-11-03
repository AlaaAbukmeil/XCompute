/* (C)2024 */
package com.example.exchange.db;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/** RepositoryExample class represents an example repository component. */
@Repository
public class RepositoryExample {

  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public RepositoryExample(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public List<Map<String, Object>> queryForList(String sql, Object... args)
      throws DataAccessException {
    return jdbcTemplate.queryForList(sql, args);
  }

  public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... args)
      throws DataAccessException {
    return jdbcTemplate.query(sql, rowMapper, args);
  }

  public int update(String sql, Object... args) throws DataAccessException {
    return jdbcTemplate.update(sql, args);
  }

  public <T> T queryForObject(String sql, Class<T> requiredType, Object... args)
      throws DataAccessException {
    return jdbcTemplate.queryForObject(sql, requiredType, args);
  }
}
