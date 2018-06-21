#import "PdfViewer.h"

@interface PdfViewer()
@property (nonatomic, retain) NSString *commandId;
@end


@implementation PdfViewer
@synthesize commandId;

- (BOOL)isIpad
{
    return UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad;
}

- (void)openDocument:(CDVInvokedUrlCommand *)command
{
    self.commandId = command.callbackId;
    NSString *urlString = (NSString *)command.arguments[0];		
    NSString *nibName = [self isIpad] ? @"PDFViewController" : @"PDFViewController_iPhone";
    PDFViewController *pdfViewer = [[PDFViewController alloc] initWithNibName:nibName bundle:nil];
    [pdfViewer setUrlToPDF:[NSURL URLWithString:urlString]];
    [self.viewController presentViewController:pdfViewer animated:YES completion:NULL];
}

@end
