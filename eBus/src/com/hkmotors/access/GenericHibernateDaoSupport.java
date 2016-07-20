package com.hkmotors.access;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.hkmotors.entity.QueryResult;

/**
 * DAO的泛型模板
 * 
 * @company yeagiaro
 * @author george.zhao
 * @date 2014-12-5 下午3:02:53
 * 
 * @param <T>
 * @param <PK>
 */
public abstract class GenericHibernateDaoSupport<T, PK extends Serializable>
		extends HibernateDaoSupport {
	/**
	 * 为父类HibernateDaoSupport注入sessionFactory的值
	 * 
	 * @param sessionFactory
	 */
	@Resource(name = "sessionFactory")
	public void setSuperSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	/**
	 * 保存transientInstance至数据库
	 * 
	 * @param transientInstance
	 */
	public abstract void save(T transientInstance);

	/**
	 * 从数据库删除persistentInstance
	 * 
	 * @param persistentInstance
	 */
	public abstract void delete(T persistentInstance);

	/**
	 * 通过主键id从数据库找到T对象
	 * 
	 * @param id
	 *            主键
	 * @return id对应的对象
	 */
	public abstract T findById(PK id);

	/**
	 * 从数据库找到所有和符合示例对象instance属性一致的对象集合 <br>
	 * 特别注意：<br>
	 * 1)ID不作为过滤条件<br>
	 * 2)如果Example的条件属性有对象的话，例如 <br>
	 * DataMatch match = new DataMatch(); <br>
	 * match.setDataGame(new DataGame(1L)); <br>
	 * matchDao.findByExample(match); <br>
	 * 生成的SQL语句条件都会变成where(1=1)。 <br>
	 * 如果 DataMatch match = new DataMatch(); <br>
	 * match.setDataGame(new DataGame(1L));<br>
	 * match.setRound(2L);<br>
	 * matchDao.findByExample(match);<br>
	 * 生成的SQL语句条件只会有round=2,
	 * 所以所有Example类field为类对象的时候,DAO.findByExample或者其他使用Session
	 * ().createCriteria接口的查询都不能得到正确结果<br>
	 * 请直接使用HQL 这种情况是由于Hibernate的一个还没有修改的bug，参见
	 * http://opensource.atlassian.com/projects/hibernate/browse/HB-417
	 * 
	 * @param instance
	 *            示例对象，为Null的属性不作为匹配条件
	 * @return 对象集合
	 */
	public abstract List<T> findByExample(T instance);

	/**
	 * 以“select * from T where T.propertyName = value”的条件查询数据
	 * 
	 * @param propertyName
	 *            T的field名字
	 * @param value
	 *            propertyName的条件值
	 * @return
	 */
	public abstract List<T> findByProperty(String propertyName, Object value);

	/**
	 * 找出所有的T对象
	 */
	public abstract List<T> findAll();

	/**
	 * 如果session中存在相同持久化标识(identifier)的实例，用用户给出的对象的状态覆盖旧有的持久实例
	 * 如果session没有相应的持久实例，则尝试从数据库中加载，或创建新的持久化实例 最后返回该持久实例
	 * 用户给出的这个对象没有被关联到session上，它依旧是脱管的
	 * 
	 * @param detachedInstance
	 *            用户给出的待合并对象
	 * @return 合并后的实例
	 */
	public abstract T merge(T detachedInstance);

	/**
	 * 更新脏数据<br>
	 * 具体代码没看过，不清楚其具体影响，欢迎补充
	 * 
	 * @param instance
	 */
	public abstract void attachDirty(T instance);

	/**
	 * 锁定instance <br>
	 * 具体代码没看过，不清楚其具体影响，欢迎补充
	 * 
	 * @param instance
	 */
	public abstract void attachClean(T instance);

	/**
	 * 实现数据翻页查询
	 * 
	 * @param example
	 *            根据example中的不为空的属性作为查询条件
	 * @param start
	 *            忽略前面多少条记录,如果为null,从0开始
	 * @param limit
	 *            最多取出多少条记录，如果为null，无限制
	 * @param order
	 *            排序
	 * @return QueryResult<T> 对象包含去除记录限制的总记录数和有此限制下的所有记录的集合
	 */
	public QueryResult<T> findData(T example, Integer start, Integer limit,
			Order order) {
		// 获得统计信息
		Long total = (Long) (getSession().createCriteria(example.getClass())
				.add(Example.create(example))
				.setProjection(Projections.rowCount()).uniqueResult());

		// 获得数据集
		Criteria criteria = getSession().createCriteria(example.getClass())
				.add(Example.create(example));
		if (order != null) {
			criteria.addOrder(order);
		}
		if (start != null && start.intValue() > 0)
			criteria.setFirstResult(start);
		if (limit != null && limit.intValue() > 0)
			criteria.setMaxResults(limit);
		@SuppressWarnings("unchecked")
		List<T> list = criteria.list();
		criteria.setMaxResults(0);

		return new QueryResult<T>(list, (total == null) ? 0 : total.intValue());
	}

	/**
	 * 实现数据翻页查询
	 * 
	 * @param example
	 *            根据example中的不为空的属性作为查询条件
	 * @param start
	 *            忽略前面多少条记录,如果为null,从0开始
	 * @param limit
	 *            最多取出多少条记录，如果为null，无限制
	 * @return QueryResult<T> 对象包含去除记录限制的总记录数和有此限制下的所有记录的集合
	 */
	public QueryResult<T> findData(T example, Integer start, Integer limit) {
		return findData(example, start, limit, null);
	}

	/**
	 * 实现数据翻页查询
	 * 
	 * @param queryHql
	 *            查询条件hql
	 * @param start
	 *            忽略前面多少条记录，如果为null,从0开始
	 * @param limit
	 *            最多取出多少条记录，如果为null，无限制
	 * @return QueryResult<T> 对象包含去除记录限制的总记录数和有此限制下的所有记录的集合
	 */
	public QueryResult<T> findData(String queryHql, Integer start, Integer limit) {
		return findData(queryHql, null, start, limit);
	}

	/**
	 * 实现数据翻页查询
	 * 
	 * @param queryHql
	 *            查询条件hql
	 * @param parameters
	 *            查询条件的参数
	 * @param start
	 *            忽略前面多少条记录，如果为null,从0开始
	 * @param limit
	 *            最多取出多少条记录，如果为null，无限制
	 * @return QueryResult<T> 对象包含去除记录限制的总记录数和有此限制下的所有记录的集合
	 * @version Feb 25, 2010 2:00:33 PM
	 */
	public QueryResult<T> findData(String queryHql, List<Object> parameters,
			Integer start, Integer limit) {
		// 获得统计信息 Integer totalRs =
		System.out.println("*****queryHql1*****" + queryHql + "*******");
		int fromIndex = queryHql.toLowerCase().indexOf("from");

		String countHql = "select count(*) " + queryHql.substring(fromIndex);

		Query query = getSession().createQuery(countHql);
		if (parameters != null) {
			for (int i = 0; i < parameters.size(); i++)
				query.setParameter(i, parameters.get(i));
		}
		Long total = (Long) (query.uniqueResult());

		// 获得数据集
		System.out.println("*****queryHql2*****" + queryHql + "*******");
		query = getSession().createQuery(queryHql);
		if (parameters != null) {
			for (int i = 0; i < parameters.size(); i++)
				query.setParameter(i, parameters.get(i));
		}

		if (start != null && start.intValue() > 0)
			query.setFirstResult(start);
		if (limit != null && limit.intValue() > 0)
			query.setMaxResults(limit);
		List<T> list = query.list();
		query.setMaxResults(0);

		return new QueryResult<T>(list, (total == null) ? 0 : total.intValue());
	}

	/**
	 * 根据hql和过滤条件，自动生成过滤的hql语句，并返回查询结果
	 * 
	 * @param queryHql
	 *            hql语句
	 * @param start
	 * @param limit
	 * @param resources
	 *            可供过滤的资源
	 * @param filters
	 *            过滤class和属性的对应
	 * @return
	 */
	public QueryResult<T> findData(String queryHql, Integer start,
			Integer limit, Map<Class, List<Integer>> resources,
			Map<Class, String> filters) {
		String filterHql = filter(resources, filters);
		if (filterHql == null) {
			return new QueryResult<T>(new ArrayList<T>(), 0);
		} else if (filterHql.isEmpty()) {
			return findData(queryHql, start, limit);
		} else {
			final String where = "where";
			int whereIndex = queryHql.toLowerCase().indexOf(where);
			final String and = " and";
			if (whereIndex < 0) {
				if (filterHql.startsWith(and)) {
					filterHql = where + filterHql.substring(and.length());
				}
				return findData(queryHql + filterHql, start, limit);
			} else {
				filterHql = where + filterHql.substring(and.length()) + and
						+ queryHql.substring(whereIndex + where.length());
				return findData(queryHql.substring(0, whereIndex) + filterHql,
						start, limit);
			}
		}
	}

	/**
	 * 根据hql和过滤条件，自动生成过滤的hql语句，并返回查询结果
	 * 
	 * @author jiazi.hou
	 * @since 7978
	 * @param queryHql
	 *            hql语句
	 * @param resources
	 *            可供过滤的资源
	 * @param filters
	 *            过滤class和属性的对应
	 * @return
	 */
	public List<T> getData(String queryHql,
			Map<Class, List<Integer>> resources, Map<Class, String> filters) {
		String filterHql = filter(resources, filters);
		Query query = null;
		if (filterHql == null || filterHql.isEmpty()) {
			query = getSession().createQuery(queryHql);
		} else {
			final String where = "where";
			int whereIndex = queryHql.toLowerCase().indexOf(where);
			final String and = " and";
			if (whereIndex < 0) {
				if (filterHql.startsWith(and)) {
					filterHql = where + filterHql.substring(and.length());
				}
			} else {
				filterHql = where + filterHql.substring(and.length()) + and
						+ queryHql.substring(whereIndex + where.length());
			}
			query = getSession().createQuery(queryHql + filterHql);
		}
		List<T> list = query.list();
		query.setMaxResults(0);
		return list;
	}

	/**
	 * 
	 * @param filterResources
	 *            可进行过滤的资源
	 * @param filterProperties
	 *            需要进行过滤的
	 * @return 如果返回null，则表示，过滤后，将没有任何结果返回， 如果不为空，则表示为hql的where子句中的一段，以“ and“开头
	 */
	private String filter(Map<Class, List<Integer>> filterResources,
			Map<Class, String> filterProperties) {
		String hql = "";
		for (Map.Entry<Class, String> keySet : filterProperties.entrySet()) {
			Class<?> filterKey = keySet.getKey();
			List<Integer> list = filterResources.get(filterKey);
			if (list != null) {
				if (list.isEmpty()) {
					return null;
				} else {
					String ids = "";
					for (Integer id : list) {
						ids += id.toString() + ",";
					}
					if (!ids.isEmpty())
						ids = ids.substring(0, ids.length() - 1);
					hql += " and " + keySet.getValue() + " in (" + ids + ")";
				}
			}
		}
		return hql;
	}

	/**
	 * 得到记录数
	 * 
	 * @param queryHql
	 * @return
	 */
	public Long getCount(String queryHql) {
		// 获得统计信息 Integer totalRs =
		int fromIndex = queryHql.toLowerCase().indexOf("from");
		String countHql = "select count(*) " + queryHql.substring(fromIndex);
		return (Long) (getSession().createQuery(countHql).uniqueResult());
	}

	/**
	 * 得到记录数
	 * 
	 * @param example
	 * @return
	 */
	public Long getCount(T example) {
		// 获得统计信息
		return (Long) (getSession().createCriteria(example.getClass())
				.add(Example.create(example))
				.setProjection(Projections.rowCount()).uniqueResult());
	}

	/***
	 * 
	 * 批量插入数据
	 * 
	 * @param objectList
	 */
	public void batchInsert(List objectList) {
		Transaction tx = null;
		// 得到当前session
		Session session = this.getHibernateTemplate().getSessionFactory()
				.openSession();
		// 开启事务
		try {
			tx = session.beginTransaction();
			for (int i = 0; i < objectList.size(); i++) {
				session.save(objectList.get(i));
				// 以30个数据作为一个处理单元
				if (i % 30 == 0) {
					// 只是将Hibernate缓存中的数据提交到数据库，保持与数据库数据的同步
					session.flush();
					// 清除内部缓存的全部数据，及时释放出占用的内存
					session.clear();
				}
			}
			// 提交事务
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null) {
				// 事务回滚
				tx.rollback();
			}
			// 抛出异常
			throw new RuntimeException("批量插入数据发生错误...");
		} finally {
			// 关闭事务
			session.close();
		}
	}

	/**
	 * 存储过程处理业务 <example> eg:call test(?,?,?); 则storedProdurceName=test,
	 * parameters=["json",1001,"vFlag"]
	 * storedProdurceName("test","json",1001,"vFlag"); </example>
	 * 
	 * @param storedProdurceName
	 *            调用存储过程的名
	 * @param parameters
	 *            参数
	 * 
	 */
	@SuppressWarnings("deprecation")
	public void callStoredProcedure(String storedProdurceName,
			Object... parameters) {
		StringBuffer buff = new StringBuffer("{ call ");
		buff.append(storedProdurceName).append("(");
		for (int i = 0; i < parameters.length; i++) {
			buff.append("?,");
		}
		buff.deleteCharAt(buff.length() - 1);
		buff.append(") }");
		// 得到当前session
		Session session = this.getHibernateTemplate().getSessionFactory()
				.openSession();
		Connection connect = null;
		try {
			connect = session.connection();
			CallableStatement cs = connect.prepareCall(buff.toString());
			int i = 1;
			for (Object p : parameters) {
				cs.setObject(i++, p);
			}
			cs.execute();
			connect.close();
		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

}