package views

import views.behaviours.ViewBehaviours
import views.html.AgentOverviewView

class AgentOverviewViewSpec extends ViewBehaviours {

  "AgentOverview view" must {

    val view = viewFor[AgentOverviewView](Some(emptyUserAnswers))

    val applyView = view.apply()(fakeRequest, messages)

    behave like normalPage(applyView, "agentOverview")

    behave like pageWithBackLink(applyView)
  }
}
