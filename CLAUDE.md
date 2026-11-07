# Intel: notes for Claude

Read `docs/style-guide.html` before changing anything visual. Tony's rules: one task text size (Regular, labels Medium), charcoal #1C1C1C ground, sentence case, no eyebrows except "Objective N", no grey text, square corners, 8 px grid, UK English, no em dashes.

## Writing the week (`weeks/current.json`)

The in-game objectives are deliberately cryptic ("civilians are being hunted in East Mall" means a Hostage Rescue), and each one only unlocks after the last is done. The community decodes them, so the guides are the source:

1. Read the week's decoders: Destructoid and Sportskeeda ("Division 2 <season> week <n> Manhunt"), plus Reddit's r/thedivision weekly threads.
2. Write only what at least two sources agree on. If they differ on a detail (one specific control point), keep it general ("any Black Tusk control point").
3. Each task: one block, verb and count first, always name the faction and the activity type in plain words (Hostage Rescue, Resource Convoy, Control Point), a place or tip only if it saves time. About 120 characters max. Rewrite, never copy.
4. If an objective is not decoded yet, say so plainly ("Not decoded yet. Check again on Thursday.") and check again later in the week.
5. New week: new `id` (season plus scout number), `live` date, `sources`. Climax week may be one task.
6. Final scout of a season: finishing it unlocks the season's final boss straight away, not the following Tuesday. So the `done` text for the last scout must name the boss, where to find them and how to start it ("Final boss unlocked. Take down <boss>, the <faction> boss, at <place>. Fast travel from the Manhunt tab."). Check how many scouts the season has (Red Horizon had 11) so the last week is never missed.
7. Between seasons, when there is no new scout: leave the last week in place and set `done` to say the season is over and the next one has not started.
