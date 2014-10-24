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
package org.primaresearch.web.layouteditor.client;

import com.google.gwt.i18n.client.Constants;

public interface AppConstants extends Constants {
	
	//Data constants
	String VirtualKeyboardSourceUrl();
	String HeaderHeight();
	String IntitialRegionLabelPanelWidth();
	String HelpDialogWidth();
	String HelpDialogHeight();
	String ErrorMessageDisplayTimeInMilliseconds();
	
	//Text constants
	String ButtonTooltipCreateNewObject();
	String ButtonTooltipDeleteObject();
	String ButtonTooltipEditOutline();
	String ButtonTooltipEditText();
	String ButtonTooltipRevertChanges();
	String ButtonTooltipSaveChanges();
	String ButtonTooltipSelectNextObject();
	String ButtonTooltipSelectPreviousObject();
	String ButtonTooltipShowHelp();
	String ButtonTooltipViewTextContent();
	String ButtonTooltipZoomIn();
	String ButtonTooltipZoomOut();
	String ButtonTooltipZoomToFit();
	String ButtonTooltipZoomToWidth();
	String ContentToggleButtonCaptionRegions();
	String ContentToggleButtonCaptionTextLines();
	String DocumentPageHeadingPrefix(); 
	String DocumentPageHeadingUnknownDocument();
	String ErrorMessageLogonFailed();
	String ErrorMessageSyncError();
	String LabelEditorHeadingChooseLabel();
	String LabelEditorHeadingCreateRegion();
	String LabelEditorHeadingLabel();
	String LoadingDocument();
	String MessagePromptRevertChanges();
	String MessageSaveSuccessful();
	String RevertDialogButtonCaptionCancel();
	String RevertDialogButtonCaptionRevert();
	String TextDialogButtonCaptionApply();
	String TextDialogButtonCaptionClose();
	String ToolbarSectionHeadingChanges();
	String ToolbarSectionHeadingContent();
	String ToolbarSectionHeadingZoom();
	
	String	RegionLabelParagraph();
	String	RegionLabelTitle();
	String	RegionLabelParatext();
	String	RegionLabelPrintersMark();
	String	RegionLabelPageHeader();
	String	RegionLabelPageNumber();
	String	RegionLabelOtherText();
	String	RegionLabelImage();
	String	RegionLabelDecoration();
	String	RegionLabelSeparator();
	String	RegionLabelOther();
	String	RegionLabelDescriptionParagraph();
	String	RegionLabelDescriptionTitle();
	String	RegionLabelDescriptionParatext();
	String	RegionLabelDescriptionPrintersMark();
	String	RegionLabelDescriptionPageHeader();
	String	RegionLabelDescriptionPageNumber();
	String	RegionLabelDescriptionOtherText();
	String	RegionLabelDescriptionImage();
	String	RegionLabelDescriptionDecoration();
	String	RegionLabelDescriptionSeparator();
	String	RegionLabelDescriptionOther();
	
	String ErrorMessageContentLoadingFailed();
	String ErrorMessageContentObjectAddingFailed();
	String ErrorMessagePageIdLoadingFailed();
	String ErrorMessageContentObjectDeletionFailed();
	String ErrorMessageTextContentSyncFailed();
	String ErrorMessageRegionTypeSyncFailed();
	String ErrorMessageObjectOutlineSyncFailed();
	String ErrorMessagePageFileSaveFailed();
	String ErrorMessageRevertChangesFailed();
	String ErrorMessageLoadingVirtualKeyboardFailed();

}
