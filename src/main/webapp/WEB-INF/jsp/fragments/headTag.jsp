<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>

    _csrf.token: ${_csrf.token} <br>
    _csrf.headerName: ${_csrf.headerName} <br>

    <title><spring:message code="app.title"/></title>
    <base href="${pageContext.request.contextPath}/"/>

<%--    <a href='${pageContext.request.contextPath}?language=ru'>Russian</a>--%>
<%--    ${pageContext.request.contextPath} <br>--%>
<%--    ${pageContext.request.requestURL} <br>--%>

<%--    <a href='?language=ru'>Russian</a>--%>
<%--    <a href='?language=en'>English</a>  <!-- (2) --> <br>--%>
    <a href='${requestScope['javax.servlet.forward.request_uri']}?language=en'>English</a>  <br>
    <a href='${requestScope['javax.servlet.forward.request_uri']}?language=ru'>Russian</a>  <br>
    Current Locale : ${pageContext.response.locale}<br>
<%--    contextPath : ${pageContext.request.contextPath}<br>--%>
    requestScope: ${requestScope['javax.servlet.forward.request_uri']}<br>


    <link rel="stylesheet" href="resources/css/style.css?v=2">
    <link rel="stylesheet" href="webjars/bootstrap/4.6.0-1/css/bootstrap.min.css">
    <link rel="stylesheet" href="webjars/noty/3.1.4/demo/font-awesome/css/font-awesome.min.css">
    <link rel="stylesheet" href="webjars/datatables/1.10.24/css/dataTables.bootstrap4.min.css">
    <link rel="stylesheet" href="webjars/noty/3.1.4/lib/noty.css"/>
    <link rel="stylesheet" href="webjars/datetimepicker/2.5.20-1/jquery.datetimepicker.css">
    <link rel="shortcut icon" href="resources/images/icon-meal.png">

    <%--http://stackoverflow.com/a/24070373/548473--%>
    <script src="webjars/jquery/3.6.0/jquery.min.js"></script>
    <script src="webjars/bootstrap/4.6.0-1/js/bootstrap.min.js" defer></script>
    <script src="webjars/datatables/1.10.24/js/jquery.dataTables.min.js" defer></script>
    <script src="webjars/noty/3.1.4/lib/noty.min.js" defer></script>
    <script src="webjars/datatables/1.10.24/js/dataTables.bootstrap4.min.js" defer></script>
    <script src="webjars/datetimepicker/2.5.20-1/build/jquery.datetimepicker.full.min.js" defer></script>
</head>