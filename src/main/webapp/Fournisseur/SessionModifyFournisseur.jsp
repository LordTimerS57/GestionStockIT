<%--
  Created by IntelliJ IDEA.
  User: Ainar
  Date: 22/10/2025
  Time: 10:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String tag = request.getParameter("id");
    session.setAttribute("Tag_fournisseur", tag);
    response.sendRedirect(request.getContextPath()+ "/Fournisseurs/Modification" );
%>
