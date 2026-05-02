<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/layout.jsp" %>

<h1>
    <c:choose>
        <c:when test="${not empty play.id}">Редактирование спектакля</c:when>
        <c:otherwise>Добавление спектакля</c:otherwise>
    </c:choose>
</h1>

<c:if test="${not empty error}">
    <div class="error">${error}</div>
</c:if>

<div class="form-container">
    <form method="post" action="${pageContext.request.contextPath}/plays/save">
        <c:if test="${not empty play.id}">
            <input type="hidden" name="id" value="${play.id}"/>
        </c:if>
        <input type="hidden" name="theaterId" value="${theater.id}"/>

        <div class="form-group">
            <label>Театр</label>
            <div class="field-readonly">${theater.name}</div>
        </div>

        <div class="form-group">
            <label for="title">Название спектакля *</label>
            <input type="text" id="title" name="title"
                   value="${play.title}" required maxlength="200"/>
        </div>

        <div class="form-group">
            <label for="directorId">Режиссёр *</label>
            <select id="directorId" name="directorId" required>
                <option value="">— Выберите режиссёра —</option>
                <c:forEach var="d" items="${directors}">
                    <option value="${d.id}"
                        <c:if test="${d.id == play.director.id}">selected</c:if>>${d.name}</option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group">
            <label>Актёры</label>
            <div class="checkbox-list">
                <c:forEach var="actor" items="${actors}">
                    <label>
                        <input type="checkbox" name="actorIds" value="${actor.id}"
                            <c:if test="${selectedActorMap[actor.id]}">checked</c:if>/>
                        ${actor.name}
                    </label>
                </c:forEach>
                <c:if test="${empty actors}">
                    <span>Нет доступных актёров</span>
                </c:if>
            </div>
        </div>

        <div class="form-group">
            <label for="durationMinutes">Продолжительность (минуты) *</label>
            <input type="number" id="durationMinutes" name="durationMinutes"
                   value="${play.durationMinutes != null ? play.durationMinutes : ''}"
                   min="1" required/>
        </div>

        <div class="form-group">
            <label for="priceParterre">Цена в партер (руб.) *</label>
            <input type="number" id="priceParterre" name="priceParterre"
                   value="${play.priceParterre != null ? play.priceParterre : 0}"
                   min="0" required/>
        </div>

        <div class="form-group">
            <label for="priceBalcony">Цена на балкон (руб.) *</label>
            <input type="number" id="priceBalcony" name="priceBalcony"
                   value="${play.priceBalcony != null ? play.priceBalcony : 0}"
                   min="0" required/>
        </div>

        <div class="form-group">
            <label for="priceMezzanine">Цена в бельэтаж (руб.) *</label>
            <input type="number" id="priceMezzanine" name="priceMezzanine"
                   value="${play.priceMezzanine != null ? play.priceMezzanine : 0}"
                   min="0" required/>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn btn-primary">Сохранить</button>
            <a href="${pageContext.request.contextPath}/plays?theaterId=${theater.id}"
               class="btn btn-secondary">Отмена</a>
        </div>
    </form>
</div>

</div></body></html>