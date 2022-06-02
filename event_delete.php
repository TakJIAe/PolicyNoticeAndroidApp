<?php 

    error_reporting(E_ALL); 
    ini_set('display_errors',1); 

    include('dbcon.php');

    //POST 값을 읽어온다.
    $time = isset($_POST['time']) ? $_POST['time'] : '';
    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


    $sql="select * from event where time='$time'";
    $stmt = $con->prepare($sql);
    $stmt->execute();

    try{
        $sql = "delete from event where time='$time'";
        //sql문 실행하여 데이터를 MYSQL 서버의 favorite 테이블에서 제거
        $stmt = $con->prepare($sql);
        $stmt->execute();
    } catch(PDOException $e) {
        die("Database error: " . $e->getMessage()); 
    }
    
?>

<?php 
    if (isset($errMSG)) echo $errMSG;
    if (isset($successMSG)) echo $successMSG;

 $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
   
    if( !$android )
    {
?>
    <html>
       <body>
            <form action="<?php $_PHP_SELF ?>" method="POST">
                title: <input type = "text" name = "title" />
                startdate: <input type = "text" name = "startdate" />
                enddate: <input type = "text" name = "enddate" />
                date: <input type = "text" name = "date" />
                time: <input type = "text" name = "time" />
                <input type = "submit" name = "submit" />
            </form>
       
       </body>
    </html>

<?php 
    }
?>