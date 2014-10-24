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
 * Checks if a user is allowed to access a specific file
 *
 * This function is NOT available via SOAP.
 *
 * @copyright PRImA June 2013
 * @param  string Uid      Username
 * @param  string fileID   The ID of the file to check permissions (could be Did, Aid depending on fileType)
 * @param  string fileType The type of the file to check permissions (fullViewJPEG, attachment)
 * @return boolean         TRUE: allowed, FALSE: not allowed
 */
function allowedToAcceess($Uid, $fileID, $fileType)
{
	//This function can be used to check for access permissions for a particular user.
	//In this example it always returns TRUE.
	return TRUE;
}

/*
 * Returns URI for a specific file
 *
 * This function is NOT available via SOAP.
 *
 * @copyright PRImA June 2013
 * @param  string Uid      Username
 * @param  string fileID   The ID of the requested file (could be Did, Aid depending on fileType)
 * @param  string fileType The type of the file needed (fullViewJPEG, attachment)
 * @return string          URI of requested file
 */

function getFileURI($Uid, $fileID, $fileType)
{
	include("../config.inc.php");
	switch ($fileType)
	{
		case 'fullViewJPEG':
			return (allowedToAcceess($Uid, $Did, 'fullViewJPEG')?"http://www.prima.cse.salford.ac.uk/emop-dataset/" . $DATA['Documents'][$fileID]['fullview']:"");
			break;
		case 'attachment':
			return (allowedToAcceess($Uid, $Aid, 'attachment')?"http://www.prima.cse.salford.ac.uk/emop-dataset/" . $DATA['Attachments'][$fileID]['path']:"");
			break;
		default:
			return "";
	}
}

/*
 * Returns XML with source URIs for WebLayoutEditor
 *
 * This function is available via SOAP.
 *
 * @copyright PRImA June 2013
 * @param  string Uid Username
 * @param  string Aid Attachment ID
 * @return string     XML stream
 */
function getDocumentAttachmentSources($Uid, $Aid) {
	include("../config.inc.php");

	$Did = $DATA['Attachments'][$Aid]['Did'];
	$ATid = "PAGE";
	$urlFullViewJpeg = getFileURI($Uid, $Did, 'fullViewJPEG');
	$urlAttachment = getFileURI($Uid, $Aid, 'attachment');

	$xml = new XMLWriter();
	$xml->openMemory();
	$xml->setIndent(true);
	$xml->setIndentString("\t");
	$xml->startDocument('1.0', 'utf-8');
	$xml->startElement("DocumentRepository");
	$xml->startElement("DocumentAttachmentSources");
	$xml->startElement("ImageSource");
	$xml->writeAttribute("documentId", $Did);
	$xml->writeAttribute("type", "fullview");
	$xml->text("$urlFullViewJpeg");
	$xml->endElement();
	$xml->startElement("AttachmentSource");
	$xml->writeAttribute("attachmentId", $Aid);
	$xml->writeAttribute("attachmentTypeId", $ATid);
	$xml->text("$urlAttachment");
	$xml->endElement();
	$xml->endElement();
	$xml->endElement();
	$xml->endDocument();

	return $xml->outputMemory(TRUE);
}

/*
 * Returns XML perimissions for a specific document
 *
 * This function is available via SOAP.
 *
 * @copyright PRImA June 2013
 * @param  string Uid Username
 * @param  string Aid Attachment ID
 * @return string     XML stream
 */
function getDocumentAttachmentPermissions($Uid, $Aid) {
	include("../config.inc.php");

	$Did = $DATA['Attachments'][$Aid]['Did'];

	$xml = new XMLWriter();
	$xml->openMemory();
	$xml->setIndent(true);
	$xml->setIndentString("\t");
	$xml->startDocument('1.0', 'utf-8');
	$xml->startElement("DocumentRepository");
	$xml->startElement("DocumentAttachmentPermissions");
	$xml->writeAttribute("attachmentId", $Aid);
	$xml->startElement("Permission");
	$xml->writeAttribute("name", "a");
	$xml->endElement();
	$xml->endElement();
	$xml->endElement();
	$xml->endDocument();

	return $xml->outputMemory(TRUE);
}

/*
 * Returns XML with source URIs for WebLayoutEditor
 *
 * This function is available via SOAP.
 *
 * If mode=new, return a DocumentAttachmentSources - AttachmentSource with the
 * location for the new attachment.
 *
 * @copyright PRImA June 2013
 * @param  string     Uid  Username
 * @param  string     Aid  Attachment ID
 * @param  string     mode enum(new, overwrite)
 * @param  attachment base64 encoded content of new attachment
 * @return string     XML stream
 */
function saveDocumentAttachment($Uid, $Aid, $mode, $attachment) {
	include("../config.inc.php");

	$Did = $DATA['Attachments'][$Aid]['Did'];
	$ATid = "PAGE";
	$returnCode = 0;
	$returnMessage = "";

	switch ($mode) {
		case "new":
			$AidNEW = $Aid . "." . date("Y-m-d-H-i-s", time());
			$urlAttachmentNEW = "repository/attachments/" . $AidNEW . ".xml";
			$pathNEW = __DIR__ . "/../" . $urlAttachmentNEW;
			$content = $attachment;

			break;
		case "overwrite":
			$pathNEW = __DIR__ . "/../" . $DATA['Attachments'][$Aid]['path'];
			break;
	}
	$file_put_contents_Result = file_put_contents($pathNEW, $content);
	if ($file_put_contents_Result === FALSE) {
		//failed
		$returnCode = "1";
		$returnMessage = "Error when creating file (Got back error from file_put_contents: $file_put_contents_Result)";
	}

	$xml = new XMLWriter();
	$xml->openMemory();
	$xml->setIndent(true);
	$xml->setIndentString("\t");
	$xml->startDocument('1.0', 'utf-8');
	$xml->startElement("DocumentRepository");
	$xml->startElement("SaveDocumentAttachmentResult");
	$xml->writeAttribute("attachmentId", $Aid);
	if ($mode == "new")
		$xml->writeAttribute("newAttachmentId", $AidNEW);
	$xml->writeAttribute("returnCode", $returnCode);
	$xml->startElement("ReturnMessage");
	$xml->text("$returnMessage");
	$xml->endElement();
	$xml->endElement();
	if ($mode == "new") {
		$xml->startElement("DocumentAttachmentSources");
		$xml->startElement("AttachmentSource");
		$xml->writeAttribute("attachmentId", $AidNEW);
		$xml->writeAttribute("attachmentTypeId", $ATid);
		$xml->text("$urlAttachmentNEW");
		$xml->endElement();
		$xml->endElement();
	}
	$xml->endElement();
	$xml->endDocument();

	return $xml->outputMemory(TRUE);
}

$server = new SoapServer(null, array('soap_version' => SOAP_1_2, 'uri' => "www.primaresearch.org", 'actor' => "www.primaresearch.org"));
$server->addFunction('getDocumentAttachmentSources');
$server->addFunction('getDocumentAttachmentPermissions');
$server->addFunction('saveDocumentAttachment');
$server->handle();
?>