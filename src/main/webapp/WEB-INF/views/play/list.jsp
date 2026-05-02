<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/layout.jsp" %>

<h1>
    <c:choose>
        <c:when test="${not empty theater}">
            Спектакли театра «${theater.name}»
        </c:when>
        <c:when test="${filtered}">
            Результаты поиска спектаклей
        </c:when>
        <c:otherwise>Все спектакли</c:otherwise>
    </c:choose>
</h1>

<c:if test="${not empty message}">
    <div class="message">${message}</div>
</c:if>
<c:if test="${not empty error}">
    <div class="error">${error}</div>
</c:if>

<div class="actions-row">
    <c:if test="${not empty theater}">
        <a href="${pageContext.request.contextPath}/plays/new?theaterId=${theater.id}"
           class="btn btn-success">+ Добавить спектакль</a>
    </c:if>
    <a href="${pageContext.request.contextPath}/theaters"
       class="btn btn-secondary">← Театры</a>
</div>

<table>
    <thead>
        <tr>
            <th>Название</th>
            <th>Театр</th>
            <th>Режиссёр</th>
            <th>Продолжительность</th>
            <th>Цена: партер / балкон / бельэтаж</th>
            <th>Действия</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="play" items="${plays}">
            <tr>
                <td>
                    <a href="${pageContext.request.contextPath}/sessions?playId=${play.id}">
                        ${play.title}
                    </a>
                </td>
                <td>${play.theater.name}</td>
                <td>${play.director.name}</td>
                <td>${play.formattedDuration}</td>
                <td>
                    ${play.priceParterre} /
                    ${play.priceBalcony} /
                    ${play.priceMezzanine} руб.
                </td>
                <td>
                    <c:if test="${not empty theater}">
                        <a href="${pageContext.request.contextPath}/plays/edit?id=${play.id}"
                           class="btn btn-warning">Редактировать</a>
                        <form method="post"
                              action="${pageContext.request.contextPath}/plays/delete"
                              style="display:inline"
                              onsubmit="return confirm('Удалить спектакль «${play.title}»?');">
                            <input type="hidden" name="id" value="${play.id}"/>
                            <button type="submit" class="btn btn-danger">Удалить</button>
                        </form>
                    </c:if>
                    <c:if test="${empty theater}">
                        <%-- В режиме поиска — только просмотр сеансов --%>
                        <a href="${pageContext.request.contextPath}/sessions?playId=${play.id}"
                           class="btn btn-primary">Сеансы</a>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty plays}">
            <tr><td colspan="6">Спектакли не найдены.</td></tr>
        </c:if>
    </tbody>
</table>

</div></body></html>