# v1.0
### `GuildID:ChannelID:teamID:numGamesPlayed:numWins:numLosses:numOTLosses:isStatPublisher`

---

# Variable table
| Variable Name | Purpose | DataType |
| :---: | :---: | :---:
| GuildID | Discord API ID for the server (known as Guild) | long |
| ChannelID | Discord API ID for a specific channel | long |
| teamID | NHL API team ID number | integer |
| numGamesPlay | Number of games that the team has played | integer |
| numWins | Number of wins the team has had | integer |
| numLosses | Number of non-overtime losses the team has had | integer |
| numOTLosses | Number of overtime losses the team has had | integer |
| isStatPublisher | Does this entry post foll stats (true) or post just win/loss/ot_loss (false) | boolean |