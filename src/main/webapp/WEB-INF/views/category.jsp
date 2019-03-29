<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: kerana
  Date: 22.02.19
  Time: 18:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Category</title>
</head>
<body>
    <h3>Category name: <c:out value="${category.categoryName}"/></h3>
    <h3>Description: <c:out value="${category.categoryDescription}"/></h3>
    <c:forEach items="${category.products}" var="p">
        <p>Products: <c:out value="${p.productName}"/></p>
    </c:forEach>
</body>
</html>
