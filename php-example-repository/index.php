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
 * This file is a sample index page, for the EMOP project.
 * The purpose of this mock website is to provide access to the WebLayoutEditor.
 *
 * Important information
 * - Each document has an ID. This is referred to as Did.
 * - For each document, there can be any number of PAGE files. Each PAGE file
 *   has it's own ID, referred to as Aid.
 * - This index page, for every document contains links to specific attachments
 *   (different versions of PAGE files perhaps).
 * - This is implemented in such a way, so that it is possible to keep track of
 *   changes/updates to the PAGE file, by storing each differen version as a
 *   new attachment (that has a different Aid, but is linked to the same Did.
 * - The link to the gateway web page, includes the Aid, so that it can display
 *   the correct version of the PAGE file. If the Did for the particular
 *   document is required, you should ensure that your supporting database can
 *   provide that.
 *
 * Under production circumstances, a lot of the data required for this page
 * would be retrieved from a database. In order to simplify this example, all
 * required data is stored in config.inc.php in various arrays.
 *
*/

header("Expires: Mon, 26 Jul 1997 05:00:00 GMT"); // Date in the past
header("Cache-Control: no-store, no-cache, must-revalidate"); // HTTP/1.1
header("Cache-Control: post-check=0, pre-check=0", false);
header("Pragma: no-cache");



include('config.inc.php');
?>

<h1>Welcome to the WebLayoutEditor integration example</h1>


<? foreach ($DATA['Documents'] AS $Did => $Document): ?>
	<h2>This section contains links to the WebLayoutEditor for image <?= $Did ?></h2>
	<table border="1">
		<thead>
			<tr><th rowspan="2">Aid</th><th colspan="3">WebLayoutEditor</th></tr>
			<tr><th>[hosted on PRImA]</th><th>[using localhost]</th><th>[using localhost and debug]</th></tr>
		</thead>
		<tbody>
			<? foreach ($DATA['Attachments'] AS $Aid => $Attachment): ?>
				<tr>
					<td><?= $Aid ?></td>
					<td><a href="gateway.php?Aid=<?= $Aid; ?>">Click</a></td>
					<td>
						<? if ($CONF['enableLocalLinks'] == TRUE): ?>
							<a href="gateway.php?&uselocal=1&Aid=<?= $Aid; ?>">Click</a>
						<? endif; ?>
					</td>
					<td>
						<? if ($CONF['enableDebugLinks'] == TRUE): ?>
							<a href="gateway.php?&usedebug=1&Aid=<?= $Aid; ?>">Click</a>
						<? endif; ?>
					</td>
				</tr>
			<? endforeach; ?>
		</tbody>
	</table>
<? endforeach; ?>

