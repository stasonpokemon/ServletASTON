<html>
<body>
<h2>Hello World!</h2>
<form action="/first" method="post">
    <input type="text" name="phone">
    <button type="submit">Send</button>
</form>

<br>

<form method="post" action="/file-servlet" enctype="multipart/form-data">
    <input type="text" name="author-name">
    <input type="file" name="file">
    <button>Send</button>
</form>
</body>
</html>
