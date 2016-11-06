<?php
  $host = "localhost";
  $user = "root";
  $password = "root";
  $schema = "memo";

  // to connect to mysql php needs to use mysqli() object?
  // in the 6th argument, you have to specify the path to mysql.sock, or you will get an error like no such file or directory.
  $conn = new mysqli($host, $user, $password, $schema, 3306, "/private/tmp/mysql.sock");
 error_log( "Android has set up the connection with php file.");
 if(mysqli_connect_errno($conn)) {
    echo "failed to connect to MySQL:" . mysqli_connect_error();
  }
  // decoding the json array
  $post = json_decode(file_get_contents("php://input"),true);
  $email = $post['email'];
  $password = $post['password'];
  $sql = "SELECT email, password from user where Email = '$email' and Password = '$password'";
  $result = mysqli_query($conn, $sql);
  $row = mysqli_fetch_array($result);

  if($row) {
    $response = array();
    $response['status'] = "OK";
    $response['email'] = $row['email'];
    $response['password'] =$row['password'];
    echo json_encode($response);
  } else {
    error_log("No data found!");
  }
  mysqli_close($conn);
 ?>
