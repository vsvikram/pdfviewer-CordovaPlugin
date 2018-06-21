#import "Cordova/CDVPlugin.h"
#import "PDFViewController.h"

@interface PdfViewer : CDVPlugin
- (void)openDocument:(CDVInvokedUrlCommand *)command;
@end