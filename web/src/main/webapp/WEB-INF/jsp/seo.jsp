<%@ include file="/WEB-INF/jsp/include.jsp" %>
<html>
  <head><title><c:out value="${m.tags.title}"/></title>
      <meta name="keywords" content="neXtProt,UniProt,Human,Proteins,Proteome,Proteomics">
      <meta name="description" content="<c:out value="{{m.tags.metaDescription}}"/>">
  </head>  
  <body>
    <h1><c:out value="${m.tags.h1}"/></h1>
    <p><c:out value="${m.content}"/></p>
  </body>
</html>