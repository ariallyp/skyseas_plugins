package com.skyseas.openfireplugins.push;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import org.jivesoftware.openfire.*;
import com.skyseas.openfireplugins.push.HttpPushPlugin;
import com.skyseas.openfireplugins.push.PushServlet;

public final class push_005ftest_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List _jspx_dependants;

  private javax.el.ExpressionFactory _el_expressionfactory;
  private org.apache.AnnotationProcessor _jsp_annotationprocessor;

  public Object getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
    _jsp_annotationprocessor = (org.apache.AnnotationProcessor) getServletConfig().getServletContext().getAttribute(org.apache.AnnotationProcessor.class.getName());
  }

  public void _jspDestroy() {
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;


    try {
      response.setContentType("text/html");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("\n\n\n\n");

    HttpPushPlugin plugin = (HttpPushPlugin) XMPPServer.getInstance().getPluginManager().getPlugin(HttpPushPlugin.PLUGIN_NAME);
    if (plugin != null) {
        String pushUrl = plugin.getServletUrl();

      out.write("\n\n\n<html>\n<head>\n    <title>Http Push Testing</title>\n    <meta name=\"pageID\" content=\"push\"/>\n</head>\n<body>\n<form action=\"");
      out.print( pushUrl );
      out.write("\" method=\"post\" target=\"_blank\">\n    <h2>testing :</h2>\n    <ul>\n        <li>\n            post:\n            <br/>\n            <input value=\"");
      out.print( pushUrl );
      out.write("\" />\n        </li>\n        <li>\n            packet_content:\n            <br/>\n            <textarea name=\"packet_content\" style=\"width:500px;height: 200px;\">\n&lt;message from=&#39;skysea.com&#39;&gt;\n&lt;body&gt;hi! all.&lt;/body&gt;\n&lt;/message&gt;\n            </textarea>\n        </li>\n\n        <li>\n            you current ip: ");
      out.print( request.getRemoteAddr());
      out.write(". <br />\n            you must add your ip to global property(");
      out.print( PushServlet.ALLOW_IP_LIST_KEY );
      out.write("),\n            eg. 192.168.1.3;192.168.1.2\n        </li>\n        <li>\n            <input type=\"submit\" value=\"save\" name=\"save\"/>\n        </li>\n    </ul>\n</form>\n</body>\n</html>\n\n");

    }

    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          try { out.clearBuffer(); } catch (java.io.IOException e) {}
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else log(t.getMessage(), t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
