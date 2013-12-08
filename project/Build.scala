import sbt._
import Keys._
import play.Project._

// Deadbolt instructions: http://schaloner.github.com/

object ApplicationBuild extends Build {

	val appName         = "tagger"
	val appVersion      = "1.0-SNAPSHOT"

	val appDependencies = Seq(
		javaCore,
		javaJdbc,
		javaEbean,
		"com.typesafe" %% "play-plugins-mailer" % "2.1-RC2",
		"be.objectify" %% "deadbolt-java" % "2.1-SNAPSHOT",
		"com.feth"      %%  "play-authenticate" % "0.2.4-SNAPSHOT",
		"postgresql" % "postgresql" % "9.1-901-1.jdbc4"
	)

	val main = play.Project(appName, appVersion, appDependencies).settings(
		// Add your own project settings here      
		resolvers += Resolver.url("Objectify Play Repository", url("http://schaloner.github.com/releases/"))(Resolver.ivyStylePatterns),
		resolvers += Resolver.url("Objectify Play Snapshot Repository", url("http://schaloner.github.com/snapshots/"))(Resolver.ivyStylePatterns),
		
		resolvers += Resolver.url("play-easymail (release)", url("http://joscha.github.com/play-easymail/repo/releases/"))(Resolver.ivyStylePatterns),
		resolvers += Resolver.url("play-easymail (snapshot)", url("http://joscha.github.com/play-easymail/repo/snapshots/"))(Resolver.ivyStylePatterns),

		resolvers += Resolver.url("play-authenticate (release)", url("http://joscha.github.com/play-authenticate/repo/releases/"))(Resolver.ivyStylePatterns),
		resolvers += Resolver.url("play-authenticate (snapshot)", url("http://joscha.github.com/play-authenticate/repo/snapshots/"))(Resolver.ivyStylePatterns)
	)

}
