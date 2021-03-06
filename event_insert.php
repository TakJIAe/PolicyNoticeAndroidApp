<?php 

    error_reporting(E_ALL); 
    ini_set('display_errors',1); 

    include('dbcon.php');

    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android )
    {
        //안드로이드 코드의 postParameters 변수에 적어준 일정을 가지고 값을 전달 받는다.
        $userID=$_POST['userID'];
        $title=$_POST['title'];
        $startdate=$_POST['startdate'];
        $enddate=$_POST['enddate'];
        $date=$_POST['date'];
        $time=$_POST['time'];
        
        if(empty($title)){
            $errMSG = "일정을 입력하세요.";
        }
        else if(empty($startdate)){
            $errMSG = "시작날짜를 입력하세요.";
        }
        else if(empty($enddate)){
            $errMSG = "종료날짜를 입력하세요.";
        }

        if(!isset($errMSG)) //모두 입력 되었다면
        {
            $sql="select * from event where userID='$userID'";
            $stmt = $con->prepare($sql);
            $stmt->execute();

            try{
                //SQL문을 실행하여 데이터를 MYSQL 서버의 event 테이블에 저장
                $stmt = $con->prepare('INSERT INTO event(userID, title, startdate, enddate, date, time) VALUES(:userID, :title, :startdate, :enddate, :date, :time)');
                $stmt->bindParam(':userID', $userID);
                $stmt->bindParam(':title', $title);
                $stmt->bindParam(':startdate', $startdate);
                $stmt->bindParam(':enddate', $enddate);
                $stmt->bindParam(':date', $date);
                $stmt->bindParam(':time', $time);
                if($stmt->execute())
                {
                    $successMSG = "새로운 일정을 추가했습니다.";
                }
                else
                {
                    $errMSG = "일정 추가 에러";
                }

            } catch(PDOException $e) {
                die("Database error: " . $e->getMessage()); 
            }
        }

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
        <?php 
        if (isset($errMSG)) echo $errMSG;
        if (isset($successMSG)) echo $successMSG;
        ?>
        
        <form action="<?php $_PHP_SELF ?>" method="POST">
            Title: <input type = "text" name = "title" />
            Startdate: <input type = "text" name = "startdate" />
            Enddate: <input type = "text" name = "enddate" />
            date: <input type = "text" name = "date" />
            time: <input type = "text" name = "time" />
            <input type = "submit" name = "submit" />
        </form>
   
   </body>
</html>

<?php 
    }
?>