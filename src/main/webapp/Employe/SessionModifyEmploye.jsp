<%--
  Created by IntelliJ IDEA.
  User: Ainar
  Date: 21/10/2025
  Time: 23:12
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String section = request.getParameter("section");
  if (section != null) {
    session.setAttribute("section", section);
  }
  response.sendRedirect(request.getContextPath() + "/Profil/Modification");
%>
