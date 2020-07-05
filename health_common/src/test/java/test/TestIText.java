package test;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @Author Tian Qing
 * @Daate: Created in 14:15 2020/7/4
 */
public class TestIText {
    @Test
    public void test01() throws Exception {
        //创建文件对象
        final Document document = new Document();
        // 设置文件储存位置
        PdfWriter.getInstance(document, new FileOutputStream(new File("d:\\IText.pdf")));
        //打开文档
        document.open();
        //写入数据
        BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        document.add(new Paragraph("写中文", new Font(bfChinese)));
        //doc.add(new Paragraph("Hello 传智播客"));
        //关闭文档
        document.close();
    }
}
