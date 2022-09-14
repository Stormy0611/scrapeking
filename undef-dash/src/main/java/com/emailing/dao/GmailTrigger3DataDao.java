package com.emailing.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emailing.model.GmailTrigger1Data;
import com.emailing.model.GmailTrigger2Data;
import com.emailing.model.GmailTrigger3Data;
import com.emailing.model.Message;
import com.emailing.model.Subject;

public class GmailTrigger3DataDao {

	private static final Logger logger = LoggerFactory.getLogger(GmailTrigger3DataDao.class);

	public static void saveTrackGmailMail(Session session, GmailTrigger3Data trackGmailMail) {
		try {
			session.beginTransaction();
			session.save(trackGmailMail);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method saveTrackGmailMail error : ", ex);
		}
	}

	public static void updateMessage(Session session, Message message) {
		try {
			session.beginTransaction();
			session.update(message);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method updateMessage error : ", ex);
		}
	}

	public static boolean checkIfExist(Session session, String messageId) {
		try {
			String req = "SELECT * FROM MESSAGE WHERE MESSAGEID LIKE '" + messageId + "'";

			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Message.class);
			ArrayList<Message> listMessages = (ArrayList<Message>) query.list();

			if (listMessages != null && listMessages.size() != 0)
				return true;
			else
				return false;

		} catch (Exception ex) {
			logger.error("methof checkIfExist error : ", ex);
			return false;
		}
	}

	public static Optional<Message> getLastMessage(Session session, Subject subject) {
		try {

			String req = "select top 1 message.* from message where message.subjectid = '" + subject.getSubjectId()
					+ "' order by date desc";
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Message.class);
			List<Message> listData = query.list();
			if (listData != null && listData.size() != 0)
				return Optional.of(listData.get(0));
			else
				return Optional.empty();

		} catch (Exception ex) {
			logger.error("method getLastMessage error : ", ex);
			return Optional.empty();
		}
	}

	public static Optional<List<Message>> getSubjectListMessages(Session session, Subject subject) {
		try {
			String req = "SELECT * FROM MESSAGE WHERE SUBJECTID = '" + subject.getSubjectId()
					+ "' ORDER BY SENDINGDATE ASC";

			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Message.class);
			List<Message> listData = query.list();
			if (listData != null && listData.size() != 0)
				return Optional.of(listData);
			else
				return Optional.empty();

		} catch (Exception ex) {
			logger.error("method getSubjectListMessages error : ", ex);
			return Optional.empty();
		}
	}

	public static Optional<Message> getMessageById(Session session, String messageId) {
		try {
			String req = "SELECT * FROM MESSAGE WHERE MESSAGEID = '" + messageId + "'";

			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Message.class);
			List<Message> listMessages = query.list();
			if (listMessages != null && listMessages.size() != 0)
				return Optional.of(listMessages.get(0));
			else
				return Optional.empty();

		} catch (Exception ex) {
			logger.error("method getMessageById error : ", ex);
			return Optional.empty();
		}
	}

	public static Optional<Message> getMessageByUnsubscribeToken(Session session, String token) {
		try {
			String req = "select top 1 message.* from message where  message.unsubscribeToken = '" + token + "'";

			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Message.class);
			List<Message> listData = query.list();
			if (listData != null && listData.size() != 0)
				return Optional.of(listData.get(0));
			else
				return Optional.empty();

		} catch (Exception ex) {
			logger.error("method getMessageByUnsubscribeToken error : ", ex);
			return Optional.empty();
		}
	}

	public static GmailTrigger3Data getLastSubject(Session session) {
		try {
			Query query = session.createQuery("from GmailTrigger3Data order by trigger3Id desc");
			query.setMaxResults(1);
			List<GmailTrigger3Data> trackGmailMails = (List<GmailTrigger3Data>)query.list();
			if(trackGmailMails.isEmpty())
				return null;
			return trackGmailMails.get(0);

		} catch (Exception ex) {
			logger.error("method getLastSubject error : ", ex);
			return null;
		}
	}

	public static GmailTrigger1Data getBySubjectId(Session session, String subjectId) {
		try {
			Query query = session.createQuery("from GmailTrigger1Data gtd where gtd.subjectId=:subjectId");
			query.setParameter("subjectId", subjectId);
			List<GmailTrigger1Data> gmailTrigger1Datas = (List<GmailTrigger1Data>)query.list();
			if(gmailTrigger1Datas.isEmpty())
				return null;
			return gmailTrigger1Datas.get(0);

		} catch (Exception ex) {
			logger.error("method getBySubjectId error : ", ex);
			return null;
		}
	}

	public static List<GmailTrigger3Data> getFailedEmails(Session session) {
		try {
			Query query = session.createQuery("from GmailTrigger3Data gtd where gtd.subjectId in (select s.subjectId from Subject s where sent3=false)");
			List<GmailTrigger3Data> gmailTrigger3Datas = (List<GmailTrigger3Data>)query.list();
			if(gmailTrigger3Datas.isEmpty())
				return null;
			return gmailTrigger3Datas;

		} catch (Exception ex) {
			logger.error("method getFailedEmails trigger3 error : ", ex);
			return null;
		}
	}

	public static List<String> getCampaignUniqueEmails(Session session) {
		try {
			Query query = session.createQuery("select gtd.sentFrom from GmailTrigger3Data gtd group by gtd.sentFrom");
			List<String> successEmails = (List<String>)query.list();
			if(successEmails.isEmpty())
				return null;
			return successEmails;

		} catch (Exception ex) {
			logger.error("method getCampaignUniqueEmails trigger3 error : ", ex);
			return null;
		}
	}

	public static long getSent3TrueCountByEmail(Session session, String email) {
		try {
			Query query = session.createQuery("select count(gtd.sentFrom) from GmailTrigger3Data gtd where gtd.sentFrom=:email and gtd.subjectId in (select s.subjectId from Subject s where sent3=true)");
			query.setParameter("email", email);
			
			List result = query.list();
			return result.isEmpty() ? 0 : Long.parseLong(result.get(0).toString());
			
		} catch (Exception ex) {
			logger.error("method getSent3TrueCountByEmail error : ", ex);
			return 0;
		}
	}

	public static long getSent3FalseCountByEmail(Session session, String email) {
		try {
			Query query = session.createQuery("select count(gtd.sentFrom) from GmailTrigger3Data gtd where gtd.sentFrom=:email and gtd.subjectId in (select s.subjectId from Subject s where sent3=false)");
			query.setParameter("email", email);
			
			List result = query.list();
			return result.isEmpty() ? 0 : Long.parseLong(result.get(0).toString());
			
		} catch (Exception ex) {
			logger.error("method getSent3FalseCountByEmail error : ", ex);
			return 0;
		}
	}

}
