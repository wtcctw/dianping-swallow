package com.dianping.swallow.web.servlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dianping.swallow.web.controller.MessageDumpController;

/**
 * @author mingdongli
 *
 *         2015年6月16日下午1:40:42
 */
public class FileDownloadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String filename = request.getParameter("filename");

		response.setContentType(getServletContext().getMimeType(filename));
		response.setHeader("Content-Disposition", "attachment;filename="
				+ filename);
		response.setContentType("text/html;charset=utf-8");
		String fullFileName = MessageDumpController.FILEPATH + filename;
		InputStream in = new FileInputStream(fullFileName);
		OutputStream out = response.getOutputStream();

		int b;
		while ((b = in.read()) != -1) {
			out.write(b);
		}

		in.close();
		out.close();
	}

}
