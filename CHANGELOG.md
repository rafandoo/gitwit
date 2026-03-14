# Changelog

## v1.1.0

### Breaking Changes

- :sparkles:!: Implementation of dependency injection framework (c7643fc)
- :sparkles: (lint)!: The lint command has been adjusted to be more user-friendly (36a70fa)
- :recycle: (git)!: Changed commit resolution form (e3379ba)

### New features

- changelog: New customization options and changelog generation (e5bd6d9)
- Including a specific terminal for CI (487ecb0)
- changelog: Addition of changelog generation facilitators (a837c23)
- Implementation of dependency injection framework (#65) (7aa6c7b)
- lint: Configuration to ignore commits (82bb1bb)
- lint: Include a parameter to lint an individual message (1cb3ffb)
- commit: Argument to allow creating empty commits (c11cca5)

### Bug fixes

- changelog: Adjustment to not show message when no commits for stdout process (86e0b26)
- commit: Fix on parse emojis in commit messages (759ac53)
- Bug fixes caused by branch squash (79bec0a)
- commit: Fix on commit parse regex (04ed6c5)
- changelog: Fix to filter commits with null type (#67) (cb2c1d2)
- lint: Adjustment in the interpretation of the parameters (ac29095)
- commit: Adjustment to commit parsing with breaking changes (4f5c4b1)
- commit: Fixed a commit parsing issue (105564a)
- deploy: Defining the arguments for creating the Linux desktop file (32a4263)

### Code refactoring

- changelog: Grouping of mutually exclusive options (7e16ec1)
- Corrections to the PR review (ee5a63a)
- cli: Add hierarchical resource bundle support to global help (1b72ddd)
- changelog: Include dto for grouping changelog options (1c045ad)
- Adjustment of the warning message for depreciated command (4622e73)
- git: Division of responsibilities of the GitService class (69624bb)
- changelog: Improvement in the changelog generation process (383ad30)
- di: Adjustment for DI cycle safety in prod (f4e7765)
- commit: Refactoring of commit validation methods (aa995ff)
- lint: Prevention of possible problem (60bf6c5)
- lint: Hide deprecated options (a854ee2)
- test: Creating a common entry point for production and testing (69ba64e)
- messages: Adjustment to the hook error message (a33c9ea)
- exception: Improved exception messaging (6989acb)
- messages: Improving key and message organization (5ea42ad)

### Documentation updates

- Fix broken link (041f929)
- docs: Correction of errors in the documentation (b6100ff)
- docs: Restructuring of documentation (9fbb26e)
- docs: Documentation of new customization options and changelog generation (634494b)
- docs: Changelog documentation update (5399b2c)
- Included missing documentation (cc632fc)
- docs: Documentation for the new lint configuration (00f235f)
- docs: Inclusion of the new lint parameter in the documentation (570871a)
- docs: Adjust table delimiter (a646c37)
- docs: Lint documentation update (43f5231)
- commands: New argument for the commit command (5e8b69a)

### Tests added or updated

- changelog: Correction of tests (3b78c16)
- Improve test coverage (2f0984a)
- Creating new test scenarios for changelog (8a72bce)
- Adjustment of good practices in tests (bcde7f5)
- Improvements in changelog and other tests (01927df)
- changelog: Fixed a bug in the test mock (a628577)
- lint: Adding tests for the new parameter (9205dd5)
- Adjustments in tests using commit/git mocks (f68bca0)

## v1.0.0

- 🎉 Initial release of the project.
