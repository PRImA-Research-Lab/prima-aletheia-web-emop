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
 * The following parameters will need to be retrieved from your system.
 */

//This username is sent for future use (multiuser systems that will need to
//track which changes were performed by which user)
$PARAMS['username'] = "user1";

/* Read all files in the fullview folder (JPEG images).
 * The Documents array contains the document information,
 * in the following structure:
 *   Example is for a document with ID 123:
 *   Documents[123][fullview] = "fullview/123.jpg"
 *
 * Then read all files in the attachments fonder (PAGE files).
 * The Attachments array contains the attachment information,
 * in the following structure:
 *   Example is for an attachment with ID 456 (for document ID 123):
 *   Attachments[456][Did]  = "123"
 *   Attachments[456][path] = "attachments/456.xml"
 */
foreach (scandir(__DIR__."/repository/fullview") AS $key => $value) {
	if ($value != "." && $value != "..") {
		$Did_temp = pathinfo($value, PATHINFO_FILENAME);
		$DATA['Documents'][$Did_temp]['fullview'] = "repository/fullview/" . $Did_temp . ".jpg";
	}
}
foreach (scandir(__DIR__."/repository/attachments") AS $key => $value) {
	if ($value != "." && $value != "..") {
		//$Aid = substr(pathinfo($value, PATHINFO_FILENAME), strpos(pathinfo($value, PATHINFO_FILENAME), ".")+1);
		$Aid_temp = pathinfo($value, PATHINFO_FILENAME);
		$Did_temp = substr(pathinfo($value, PATHINFO_FILENAME), 0, strpos(pathinfo($value, PATHINFO_FILENAME), ".") );
		$DATA['Attachments'][$Aid_temp]['Did'] = $Did_temp;
		$DATA['Attachments'][$Aid_temp]['path'] = "repository/attachments/" . $value;
	}
}


//The Appid is used is unique for each integration of the WebLayoutEdior tool.
//It is used by our server to recognise you and use the correct decryption key.
//It should be different for each integration application.
//This is used to log in to the PRIma Image Repository.
//If you change the example to use your own image repository,
//you might want to remove the AppId.
$PARAMS['Appid'] = "emop-dataset";

//The secret key is shared between your server and the WebLayoutEditor server.
//It is used to encrypt the authentication token passed between the two servers.
$PARAMS['secretKey'] = "CA8BC51AD641ADFAC55124FB3E000000";

/*
 * The followig parameters are for the configuration of this sample website.
 * The URLs have to be ready to accept query string parameters.
 */
$CONF['externalLinksBaseURL'] = "URL_TO_YOUR_SERVER/WebLayoutEditor?";

$CONF['enableLocalLinks'] = true;
$CONF['localLinksBaseURL'] = "http://localhost:8888/WebLayoutEditor.html?";


$CONF['enableDebugLinks'] = true;
$CONF['debugLinksBaseURL'] = "http://localhost:8888/WebLayoutEditor.html?gwt.codesvr=127.0.0.1:9997&";
?>