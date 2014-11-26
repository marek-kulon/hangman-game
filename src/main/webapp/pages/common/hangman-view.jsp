<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath == '/' ? '' : pageContext.request.contextPath}" scope="request"/>



<div class="container">
	<div class="row">
		
		<%-- 	--------------------------- IMAGE --------------------------------- --%>
        <div class="col-md-6 top-margin">
        	<div class="hangman-image-wrapper">
        		<img class="img-thumbnail" data-hangman-image="true" src="${ctx}/images/stages/0.png"/>
        	</div>
        </div>
        
        <%-- 	--------------------------- LETTERS --------------------------------- --%>
        <div class="col-md-3 top-margin">
         	<div class="row">
		        <div class="col-md-3">
		        	<a href="javascript:void(0);" class="btn btn-warning btn-circle" data-hangman-letter="a">A</a>
		        </div>
		        <div class="col-md-3">
		        	<a href="javascript:void(0);" class="btn btn-warning btn-circle" data-hangman-letter="b">B</a>
		        </div>
		        <div class="col-md-3">
		        	<a href="javascript:void(0);" class="btn btn-warning btn-circle" data-hangman-letter="c">C</a>
		        </div>
		        <div class="col-md-3">
		        	<a href="javascript:void(0);" class="btn btn-warning btn-circle" data-hangman-letter="d">D</a>
		        </div>
		    </div>
		    <div class="row">
		        <div class="col-md-3">
		        	<a href="javascript:void(0);" class="btn btn-warning btn-circle" data-hangman-letter="e">E</a>
		        </div>
		        <div class="col-md-3">
		        	<a href="javascript:void(0);" class="btn btn-warning btn-circle" data-hangman-letter="f">F</a>
		        </div>
		        <div class="col-md-3">
		        	<a href="javascript:void(0);" class="btn btn-warning btn-circle" data-hangman-letter="g">G</a>
		        </div>
		        <div class="col-md-3">
		        	<a href="javascript:void(0);" class="btn btn-warning btn-circle" data-hangman-letter="h">H</a>
		        </div>
		    </div>
		    <div class="row">
		        <div class="col-md-3">
		        	<a href="javascript:void(0);" class="btn btn-warning btn-circle" data-hangman-letter="i">I</a>
		        </div>
		        <div class="col-md-3">
		        	<a href="javascript:void(0);" class="btn btn-warning btn-circle" data-hangman-letter="j">J</a>
		        </div>
		        <div class="col-md-3">
		        	<a href="javascript:void(0);" class="btn btn-warning btn-circle" data-hangman-letter="k">K</a>
		        </div>
		        <div class="col-md-3">
		        	<a href="javascript:void(0);" class="btn btn-warning btn-circle" data-hangman-letter="l">L</a>
		        </div>
		    </div>
		    <div class="row">
		        <div class="col-md-3">
		        	<a href="javascript:void(0);" class="btn btn-warning btn-circle" data-hangman-letter="m">M</a>
		        </div>
		        <div class="col-md-3">
		        	<a href="javascript:void(0);" class="btn btn-warning btn-circle" data-hangman-letter="n">N</a>
		        </div>
		        <div class="col-md-3">
		        	<a href="javascript:void(0);" class="btn btn-warning btn-circle" data-hangman-letter="o">O</a>
		        </div>
		        <div class="col-md-3">
		        	<a href="javascript:void(0);" class="btn btn-warning btn-circle" data-hangman-letter="p">P</a>
		        </div>
		    </div>
		    <div class="row">
		        <div class="col-md-3">
		        	<a href="javascript:void(0);" class="btn btn-warning btn-circle" data-hangman-letter="q">Q</a>
		        </div>
		        <div class="col-md-3">
		        	<a href="javascript:void(0);" class="btn btn-warning btn-circle" data-hangman-letter="r">R</a>
		        </div>
		        <div class="col-md-3">
		        	<a href="javascript:void(0);" class="btn btn-warning btn-circle" data-hangman-letter="s">S</a>
		        </div>
		        <div class="col-md-3">
		        	<a href="javascript:void(0);" class="btn btn-warning btn-circle" data-hangman-letter="t">T</a>
		        </div>
		    </div>
		    <div class="row">
		        <div class="col-md-3">
		        	<a href="javascript:void(0);" class="btn btn-warning btn-circle" data-hangman-letter="u">U</a>
		        </div>
		        <div class="col-md-3">
		        	<a href="javascript:void(0);" class="btn btn-warning btn-circle" data-hangman-letter="v">V</a>
		        </div>
		        <div class="col-md-3">
		        	<a href="javascript:void(0);" class="btn btn-warning btn-circle" data-hangman-letter="w">W</a>
		        </div>
		        <div class="col-md-3">
		        	<a href="javascript:void(0);" class="btn btn-warning btn-circle" data-hangman-letter="x">X</a>
		        </div>
		    </div>
		    <div class="row">
		        <div class="col-md-3">
		        	<a href="javascript:void(0);" class="btn btn-warning btn-circle" data-hangman-letter="y">Y</a>
		        </div>
		        <div class="col-md-3">
		        	<a href="javascript:void(0);" class="btn btn-warning btn-circle" data-hangman-letter="z">Z</a>
		        </div>
		        <div class="col-md-3">
		        </div>
		        <div class="col-md-3">
		        </div>
		    </div>
        </div>
		<div class="col-md-3 top-margin">
        </div>
    </div>
    <div class="row top-margin">
        
        <%-- 	--------------------------- GUESSED WORD --------------------------------- --%>
        <div class="col-md-4">
        	<h3 data-hangman-word="true" class="hangman-word"></h3>
        </div>

		<%-- 	--------------------------- GAME STATUS --------------------------------- --%>
        <div class="col-md-8">
        	<h2 data-hangman-status="true"></h2>
        </div>
    </div>
</div>