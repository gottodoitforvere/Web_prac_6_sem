<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/layout.jsp" %>

<h1>
    <c:choose>
        <c:when test="${not empty theater.id}">Редактирование театра</c:when>
        <c:otherwise>Добавление театра</c:otherwise>
    </c:choose>
</h1>

<c:if test="${not empty error}">
    <div class="error">${error}</div>
</c:if>

<div class="form-container">
    <form method="post" action="${pageContext.request.contextPath}/theaters/save">
        <c:if test="${not empty theater.id}">
            <input type="hidden" name="id" value="${theater.id}"/>
        </c:if>

        <div class="form-group">
            <label for="name">Название театра *</label>
            <input type="text" id="name" name="name"
                   value="${theater.name}" required maxlength="200"/>
        </div>

        <div class="form-group">
            <label for="address">Адрес *</label>
            <input type="text" id="address" name="address"
                   value="${theater.address}" required maxlength="300"/>
        </div>

        <div class="form-group">
            <label for="seatsParterre">Мест в партере *</label>
            <input type="number" id="seatsParterre" name="seatsParterre"
                   value="${theater.seatsParterre != null ? theater.seatsParterre : 0}"
                   min="0" required/>
        </div>

        <div class="form-group">
            <label for="seatsBalcony">Мест на балконе *</label>
            <input type="number" id="seatsBalcony" name="seatsBalcony"
                   value="${theater.seatsBalcony != null ? theater.seatsBalcony : 0}"
                   min="0" required/>
        </div>

        <div class="form-group">
            <label for="seatsMezzanine">Мест в бельэтаже *</label>
            <input type="number" id="seatsMezzanine" name="seatsMezzanine"
                   value="${theater.seatsMezzanine != null ? theater.seatsMezzanine : 0}"
                   min="0" required/>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn btn-primary">Сохранить</button>
            <a href="${pageContext.request.contextPath}/theaters" class="btn btn-secondary">Отмена</a>
        </div>
    </form>
</div>

</div></body></html>