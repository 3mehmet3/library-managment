# Daily commit schedule

This repository uses a local macOS LaunchAgent to apply one prepared change per day from 14 to 27 November 2026 at 11:00 Europe/Istanbul time. Each run checks the repository and file hashes, runs mvn test, commits the day's changed files, and pushes to origin/main.

The prepared change packages and runner live outside this Git repository, under ~/Library/Application Support/library-schedule/. The LaunchAgent definition is ~/Library/LaunchAgents/com.example.library-daily.plist.

| Date | Planned commit |
| --- | --- |
| 14 November | add readme |
| 15 November | document usage examples |
| 16 November | add class diagram to readme |
| 17 November | use parameterized test for loan periods |
| 18 November | use parameterized tests for policies |
| 19 November | validate borrow date early |
| 20 November | add equals and hashcode by id |
| 21 November | add identity edge case tests |
| 22 November | add borrowing boundary tests |
| 23 November | improve exception messages |
| 24 November | document design decisions |
| 25 November | add patterns and solid section to readme |
| 26 November | refine code and tests |
| 27 November | final cleanup |

Check the loaded agent with launchctl print gui/$(id -u)/com.example.library-daily. Inspect ~/Library/Application Support/library-schedule/logs/daily.log after a run. To stop future runs, use launchctl bootout gui/$(id -u) ~/Library/LaunchAgents/com.example.library-daily.plist.

The runner never changes commit timestamps. If the Mac misses a day, tests fail, the working tree is dirty, or the remote has changed unexpectedly, it stops without creating a later dependent commit. A commit that was created but not pushed remains in the local history for a safe retry.
