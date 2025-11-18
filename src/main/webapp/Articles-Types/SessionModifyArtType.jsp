<%--
  Created by IntelliJ IDEA.
  User: Ainar
  Date: 22/10/2025
  Time: 10:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String section = request.getParameter("section");
  String tag = request.getParameter("id");
  if (section != null) {
    session.setAttribute("section", section);
  }
  if("Article".equals(section))
  {
    session.setAttribute("Tag_article", tag);
  } else if ("Type".equals(section)) {
    session.setAttribute("Tag_type", tag);
  }

  System.out.println(tag + " " + section);

    assert section != null;
    response.sendRedirect(request.getContextPath()+ "/Articles-Types" + ( section.equals("Type") ? "/Types" : "/Articles" ) +"/Modification" );
%>
