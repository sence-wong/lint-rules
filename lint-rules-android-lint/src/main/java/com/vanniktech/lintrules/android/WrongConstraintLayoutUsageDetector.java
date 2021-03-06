package com.vanniktech.lintrules.android;

import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.LayoutDetector;
import com.android.tools.lint.detector.api.LintFix;
import com.android.tools.lint.detector.api.XmlContext;
import java.util.Collection;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import static com.android.tools.lint.detector.api.Category.CORRECTNESS;
import static com.android.tools.lint.detector.api.Scope.RESOURCE_FILE_SCOPE;
import static com.android.tools.lint.detector.api.Severity.ERROR;

public final class WrongConstraintLayoutUsageDetector extends LayoutDetector {
  static final Issue ISSUE_WRONG_CONSTRAINT_LAYOUT_USAGE = Issue.create("WrongConstraintLayoutUsage",
      "Marks a wrong usage of the Constraint Layout.",
      "Instead of using left & right constraints start & right should be used.", CORRECTNESS, 8, ERROR,
      new Implementation(WrongConstraintLayoutUsageDetector.class, RESOURCE_FILE_SCOPE));

  @Override public Collection<String> getApplicableElements() {
    return ALL;
  }

  @Override public void visitElement(final XmlContext context, final Element element) {
    final NamedNodeMap attributes = element.getAttributes();

    for (int i = 0; i < attributes.getLength(); i++) {
      final Node item = attributes.item(i);
      final String localName = item.getLocalName();

      if (localName != null) {
        final String properLocalName = localName.replace("Left", "Start").replace("Right", "End");

        final boolean isConstraint = localName.contains("layout_constraint");
        final boolean hasLeft = localName.contains("Left");
        final boolean hasRight = localName.contains("Right");

        final boolean isAnIssue = isConstraint && (hasLeft || hasRight);

        if (isAnIssue) {
          final LintFix fix = fix()
              .name("Fix it")
              .replace()
              .text(localName)
              .with(properLocalName)
              .build();

          context.report(ISSUE_WRONG_CONSTRAINT_LAYOUT_USAGE, item, context.getNameLocation(item), "This attribute won't work with RTL. Please use " + properLocalName + " instead.", fix);
        }
      }
    }
  }
}
