package acceptancetests;

import java.util.List;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.StoryControls;
import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;

public class StoriesRunner extends JUnitStories {

    private final StoryFinder finder = new StoryFinder();
    private final int storyTimeout = 60;
    private final int storyThreads = 2;

    public StoriesRunner() {
	configuredEmbedder().embedderControls().doGenerateViewAfterStories(true).doIgnoreFailureInStories(true).doIgnoreFailureInView(true)
		.useThreads(storyThreads).useStoryTimeoutInSecs(storyTimeout);
    }

    @Override
    public Configuration configuration() {
	return new MostUsefulConfiguration()
		.useStoryControls(new StoryControls().doDryRun(false).doSkipScenariosAfterFailure(false))
		.useStoryLoader(new LoadFromClasspath(this.getClass()))
		.useStoryReporterBuilder(
			new StoryReporterBuilder().withFormats(Format.CONSOLE, Format.HTML).withFailureTrace(true).withFailureTraceCompression(true));
    }

    @Override
    public InjectableStepsFactory stepsFactory() {
	return new InstanceStepsFactory(configuration(), new AcmeTelecomSteps());
    }

    @Override
    protected List<String> storyPaths() {
	return finder.findPaths(CodeLocations.codeLocationFromClass(this.getClass()), "**/*.story", "");
    }
}
