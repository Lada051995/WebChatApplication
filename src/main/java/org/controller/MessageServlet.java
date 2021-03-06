package org.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.dao.MessageDao;
import org.dao.MessageDaoImpl;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.model.AboutMessage;
import org.storage.xml.XMLHistoryUtil;
import org.util.ServletUtil;
import org.xml.sax.SAXException;
import static org.util.MessageUtil.*;

@WebServlet(value = "/chat", loadOnStartup = 1, asyncSupported=true)
public class MessageServlet extends HttpServlet {     
	private static int ID;
	private Lock lock = new ReentrantLock();
	private int isModifiedStorage = 0;
	private static Logger logger = Logger.getLogger(MessageServlet.class.getName());
	private final static Queue<AsyncContext> storage = new ConcurrentLinkedQueue<AsyncContext>();
	public static MessageDao messageDao = new MessageDaoImpl();

	@Override
	public void init() throws ServletException {
		super.init();
		logger.info("intit have done.");
		ID = messageDao.selectAll().size();
		//loadHistory();
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		logger.info("starte doPost");
		String data = ServletUtil.getMessageBody(request);
		try {
			JSONObject json = stringToJson(data);
			AboutMessage message = jsonToMessages(json);
			logger.info(message.toJSONString());
			lock.lock();
			try {
				message.setIdNumber(ID++);
				isModifiedStorage++;
			} finally {
				lock.unlock();
			}
			System.out.println(message.toJSONString());
			logger.info("doPost has done.");
			try {
				//XMLHistoryUtil.addData(message);
               messageDao.add(message);
			} catch (SQLException e) {
				logger.error(e);
			}
		} catch (ParseException e) {
			logger.error(e);
		}
	}
	
	/*@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String token = request.getParameter(TOKEN);
		logger.info("doGet");
		try {
			if (token != null && !"".equals(token)) {
				int index = getIndex(token);
				if(isModifiedStorage == index && isModifiedStorage != 0) {
					logger.info("GET - Response status: 304");
					response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
				} else {
					String messages;
					messages = formResponse(0);//all messages
					response.setContentType(ServletUtil.APPLICATION_JSON);
					PrintWriter out = response.getWriter();
					out.print(messages);
					out.flush();
				}
			} else {
				logger.error("'token' parameter needed");
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "'token' parameter needed");
			}
		} catch (SAXException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,"error");
			logger.error(e);
		} catch (ParserConfigurationException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "error");
			logger.error(e);
		}
	}*/
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String data = ServletUtil.getMessageBody(request);
		logger.info("doPut");
		try {
			JSONObject json = stringToJson(data);
			AboutMessage message = jsonToMessages(json);
			logger.info(message.toJSONString());
			//XMLHistoryUtil.updateData(message);
			messageDao.update(message);
			isModifiedStorage++;
		} catch (ParseException e) {
			logger.error(e);
		}
	}
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
		String token = request.getParameter(TOKEN);
		logger.info("Delete");
		if (token != null && !"".equals(token)) {
			int index = getIndex(token);
			//XMLHistoryUtil.deleteDate(index);
			//messageDao.update(message);
			isModifiedStorage++;
			logger.info("delete "+index);
		}

	}
	@SuppressWarnings("unchecked")
	private String formResponse(int index) throws SAXException, IOException, ParserConfigurationException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(MESSAGES, XMLHistoryUtil.getSubTasksByIndex(index));
		jsonObject.put(TOKEN, getToken(isModifiedStorage));
		return jsonObject.toJSONString();
	}

	private void loadHistory() throws SAXException, IOException, ParserConfigurationException, TransformerException {
		if (!XMLHistoryUtil.doesStorageExist()) { // creating storage and history if not exist
			XMLHistoryUtil.createStorage();
			//addStubData();
		}
	}
	
	private void addStubData() throws ParserConfigurationException, TransformerException {
		AboutMessage[] stubTasks = {
				new AboutMessage(4, "user","Write The Chat !") };
		for (AboutMessage task : stubTasks) {
			try {
				XMLHistoryUtil.addData(task);
			} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
				logger.error(e);
			}
		}
	}
	@Override
	public void destroy() {
		System.out.print("Hi");
		super.destroy();
	}
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException {

		System.out.println("Async Servlet with thread: " + Thread.currentThread().toString());
		final AsyncContext ac = request.startAsync();
		ac.addListener(new AsyncListener() {
			public void onComplete(AsyncEvent event) throws IOException {
				System.out.println("Async complete");
				storage.remove(ac);
			}

			public void onTimeout(AsyncEvent event) throws IOException {
				System.out.println("Timed out...");
				storage.remove(ac);
			}

			public void onError(AsyncEvent event) throws IOException {
				System.out.println("Error...");
				storage.remove(ac);
			}

			public void onStartAsync(AsyncEvent event) throws IOException {
				System.out.println("Starting async...");

			}
		});

		if(getIndex(request.getParameter(TOKEN)) != isModifiedStorage || getIndex(request.getParameter(TOKEN)) == isModifiedStorage) {
			new AsyncService(ac, isModifiedStorage).run();
		} else {
			storage.add(ac);
		}
		System.out.println("Servlet completed request handling");
	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}
}
