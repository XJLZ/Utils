
/**
 <!--PDF转图片-->
  <dependency>
      <groupId>org.apache.pdfbox</groupId>
      <artifactId>pdfbox</artifactId>
      <version>2.0.20</version>
  </dependency>
*/

public void previewPdf(HttpServletResponse response, String fileName) {
        OutputStream os = null;
        BufferedImage bufferedImage = null;
        try {
            String[] split = fileName.split("\\.");
            String imagePath = pathPrefix + System.getProperty("file.separator") + split[0] + StringPool.DOT + "png";
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                // 获取文件路径
                String filePath = pathPrefix + fileName;
                // PDF转图片
                PDDocument document = PDDocument.load(new File(filePath));
                PDFRenderer renderer = new PDFRenderer(document);
                long start = System.currentTimeMillis();
                BufferedImage image = renderer.renderImageWithDPI(0, 100);
                long end = System.currentTimeMillis();
                log.info("PDF转图片，总共耗时--->{}毫秒", end - start);
                // BufferedImage srcImage = resize(image, 240, 240);//产生缩略图
                ImageIO.write(image, "png", new File(imagePath));
                bufferedImage = ImageIO.read(new FileInputStream(new File(imagePath)));
            } else {
                bufferedImage = ImageIO.read(new FileInputStream(new File(imagePath)));
            }
            response.setContentType("image/png");
            os = response.getOutputStream();
            if (bufferedImage != null) {
                ImageIO.write(bufferedImage, "png", os);
            }
        } catch (IOException e) {
            log.error("获取图片异常{}", e.getMessage());
        } finally {
            if (os != null) {
                try {
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
