<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/layout.jsp" %>

<h1>Персоны</h1>

<c:if test="${not empty message}">
    <div class="message">${message}</div>
</c:if>
<c:if test="${not empty error}">
    <div class="error">${error}</div>
</c:if>

<div class="actions-row">
    <a href="${pageContext.request.contextPath}/persons/new" class="btn btn-success">+ Добавить персону</a>
    <a href="${pageContext.request.contextPath}/theaters" class="btn btn-secondary">← Театры</a>
</div>

<table>
    <thead>
        <tr>
            <th>Имя</th>
            <th>Роль</th>
            <th>Действия</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="person" items="${persons}">
            <tr>
                <td>${person.name}</td>
                <td>
                    <c:choose>
                        <c:when test="${person.role == 'DIRECTOR'}">Режиссёр</c:when>
                        <c:when test="${person.role == 'ACTOR'}">Актёр</c:when>
                        <c:when test="${person.role == 'BOTH'}">Режиссёр и актёр</c:when>
                    </c:choose>
                </td>
                <td>
                    <a href="${pageContext.request.contextPath}/persons/edit?id=${person.id}"
                       class="btn btn-warning">Редактировать</a>
                    <form method="post"
                          action="${pageContext.request.contextPath}/persons/delete"
                          style="display:inline"
                          onsubmit="return confirm('Удалить персону «${person.name}»?');">
                        <input type="hidden" name="id" value="${person.id}"/>
                        <button type="submit" class="btn btn-danger">Удалить</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty persons}">
            <tr>
                <td colspan="3">Персоны не найдены.</td>
            </tr>
        </c:if>
    </tbody>
</table>

</div></body></html>