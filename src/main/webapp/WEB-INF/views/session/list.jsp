<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/layout.jsp" %>

<h1>Сеансы спектакля «${play.title}»</h1>

<c:if test="${not empty message}">
    <div class="message">${message}</div>
</c:if>
<c:if test="${not empty error}">
    <div class="error">${error}</div>
</c:if>

<div class="play-info">
    <p><strong>Театр:</strong> ${play.theater.name}</p>
    <p><strong>Режиссёр:</strong> ${play.director.name}</p>
    <p><strong>Актёры:</strong>
        <c:choose>
            <c:when test="${not empty play.actors}">
                <c:forEach var="actor" items="${play.actors}" varStatus="st">
                    ${actor.name}<c:if test="${!st.last}">, </c:if>
                </c:forEach>
            </c:when>
            <c:otherwise>не указаны</c:otherwise>
        </c:choose>
    </p>
    <p><strong>Продолжительность:</strong> ${play.formattedDuration}</p>
    <p><strong>Цены:</strong>
        партер — ${play.priceParterre} руб.,
        балкон — ${play.priceBalcony} руб.,
        бельэтаж — ${play.priceMezzanine} руб.
    </p>
</div>

<div class="actions-row">
    <a href="${pageContext.request.contextPath}/sessions/new?playId=${play.id}"
       class="btn btn-success">+ Добавить сеанс</a>
    <a href="${pageContext.request.contextPath}/plays?theaterId=${play.theater.id}"
       class="btn btn-secondary">← Спектакли</a>
</div>

<table>
    <thead>
        <tr>
            <th>Дата</th>
            <th>Время</th>
            <th>Свободно: партер</th>
            <th>Свободно: балкон</th>
            <th>Свободно: бельэтаж</th>
            <th>Купить билеты</th>
            <th>Действия</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="s" items="${sessions}">
            <tr>
                <td>${s.sessionDate}</td>
                <td>${s.sessionTime}</td>
                <td>${s.freeParterre}</td>
                <td>${s.freeBalcony}</td>
                <td>${s.freeMezzanine}</td>
                <td>
                    <form method="post"
                          action="${pageContext.request.contextPath}/sessions/buy">
                        <input type="hidden" name="sessionId" value="${s.id}"/>
                        <input type="hidden" name="playId" value="${play.id}"/>
                        <div class="buy-form">
                            <select name="seatType">
                                <option value="parterre">Партер</option>
                                <option value="balcony">Балкон</option>
                                <option value="mezzanine">Бельэтаж</option>
                            </select>
                            <input type="number" name="count" value="1" min="1" style="width:70px"/>
                            <button type="submit" class="btn btn-primary">Купить</button>
                        </div>
                    </form>
                </td>
                <td>
                    <a href="${pageContext.request.contextPath}/sessions/edit?id=${s.id}"
                       class="btn btn-warning">Редактировать</a>
                    <form method="post"
                          action="${pageContext.request.contextPath}/sessions/delete"
                          style="display:inline"
                          onsubmit="return confirm('Удалить сеанс ${s.sessionDate} ${s.sessionTime}?');">
                        <input type="hidden" name="id" value="${s.id}"/>
                        <button type="submit" class="btn btn-danger">Удалить</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty sessions}">
            <tr>
                <td colspan="7">Сеансы не найдены.</td>
            </tr>
        </c:if>
    </tbody>
</table>

</div></body></html>