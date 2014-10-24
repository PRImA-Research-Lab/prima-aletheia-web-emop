<?php
/*
 * Copyright 2014 PRImA Research Lab, University of Salford, United Kingdom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * This file takes as parameter the Aid and generates the access URL for the
 * WebLayoutEditor, which is being displayed inline.
 */

include('config.inc.php');


//Retrieve Aid from the URL
$Aid = $_GET['Aid'];
$Did = $DATA['Attachments'][$Aid]['Did'];


function createAuthenticationToken($username, $secretKey)
{
	$orig_json_array = json_encode(array('ip'=>$_SERVER['REMOTE_ADDR'], 'ts'=>time(), 'uid'=>$username));
	$temp = $orig_json_array;
	$secretKey = md5($secretKey);
	$initialVector = mcrypt_create_iv(mcrypt_get_iv_size(MCRYPT_RIJNDAEL_128, MCRYPT_MODE_CFB) ,MCRYPT_DEV_RANDOM);
	$temp = mcrypt_encrypt(MCRYPT_RIJNDAEL_128, $secretKey, $temp, MCRYPT_MODE_CFB, $initialVector);
	$temp = $initialVector.$temp;
	$temp = base64_encode($temp);
	return urlencode($temp);
}


$authenticationToken = createAuthenticationToken($PARAMS['username'], $PARAMS['secretKey']);

$urlBase = $CONF['externalLinksBaseURL'];

//Check if we want to use local host (for development only)
isset($_GET['uselocal'])  ? $uselocal  = $_GET['uselocal']  : $uselocal  = "0";
if ($uselocal==1) $urlBase = $CONF['localLinksBaseURL'];

//Check if we want to use debug (for development only)
isset($_GET['usedebug'])  ? $usedebug  = $_GET['usedebug']  : $usedebug  = "0";
if ($usedebug==1) $urlBase = $CONF['debugLinksBaseURL'];

$targeturl = $urlBase . "Did=$Did&Aid=$Aid&Appid=".$PARAMS['Appid']."&a=$authenticationToken";

if ($usedebug==1) {


	echo "<p><a href='$targeturl'>$targeturl</a></p>";
	//header("Location: ". $targeturl);
	}


echo "<body style=\"overflow: hidden;\">";
echo '<p><a href="javascript:history.go(-1);">Back</a></p>';

echo '<object data="'.$targeturl.'" type="text/html" style="width:100%; height:100%; margin:0px; padding: 0px;">';
	echo "<p style=\"background: MistyRose; border: DeepPink 1px solid; font: 0.8em Verdana; color: darkred; padding: 10px; \"><span style=\"text-decoration: underline; font-weight: bold;\">Notice</span><br />If you are seeing this, there is an error!</p>";
echo '</object>';
echo "</body>";

exit(0);
?>

