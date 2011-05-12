How to do a release
===================

Prerequisites:

 * Is NEWS.txt up to date?
 * Ensure all dependencies are up to date?
   * `mvn versions:display-dependency-updates`
   * `mvn versions:display-plugin-updates`
 * Ensure that all branches are up to date (git fetch origin).
   * The release plugin does a plain "git push", which pushes all branches by default.  This will fail if any branch is behind. Typically, this means the gh-pages branch.

First, read your GPG passphrase into a variable.  Unfortunately, I can't make the GPG plugin read from stdin during the release, so it has to be done this way.

    $ read -s 'pw?Password: '

Put the version into a shell variable for easy access (and cut'n'paste ability).

    $ ver=1.3.4

Next, run the release.  First, try a dry run, to check it's all OK (and download any missing plugins).

    $ mvn -DdryRun=true -DreleaseVersion="$ver" -Dtag="rel-$ver" -Dgpg.passphrase="$pw" release:prepare
    …
    $ mvn release:clean

Then, do it for real.

    $ mvn -DreleaseVersion="$ver" -Dtag="rel-$ver" -Dgpg.passphrase="$pw" release:prepare release:perform

In case this barfs after the "git push" stage, edit release.properties and change the phase from "run-preparation-goals" to "scm-commit-release".  Yes, this is utterly horrible.

Log into the [OSS Nexus (staging repositories)](https://oss.sonatype.org/index.html#stagingRepositories), and follow the [release it](https://docs.sonatype.org/display/repository/sonatype+oss+maven+repository+usage+guide#SonatypeOSSMavenRepositoryUsageGuide-10.ReleaseIt) instructions from Sonatype.

Now, change into the source code that `release:deploy` made, and rebuild for the dist profile.

    $ cd target/checkout
    $ mvn -Pdist clean verify

Create a source archive.

    $ bin/src-archive jslint4java-dist/target/jslint4java-$ver-dist.zip

With that done, upload the dist and src archives to google code.  If you are prompted for a password, use the one on your [google code profile](https://code.google.com/hosting/settings).

    $ bin/upload-to-googlecode jslint4java-dist/target/jslint4java-$ver-dist.zip

Import the documentation to google code svn.  Use the same password as before.

    $ bin/import-docs-to-googlecode jslint4java-dist/target/jslint4java-$ver-dist.zip

Now, update the [google code site](http://code.google.com/p/jslint4java/):

 * Add news to front page.
 * Update docs links.
 * Update javadocs links.
 * Add new Milestone for the next release.

Send a mail to <jslint_com@yahoogroups.com> noting the new release.

Write a blog post pointing out the new release.
