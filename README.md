# pvFilter
This plugin filters player content including commands, signs, books, item names and entity names. It can be combined with [pvChat](https://github.com/chiralpenguin/pvChat) to filter player messages.

There is a filter API for use in other plugins, enabling comprehensive filtering in alternative chat plugins and more. See an example implementation [here](https://github.com/chiralpenguin/pvChat/blob/ef30a879dd356d46671a19391b7f57dad00159a6/src/main/java/com/purityvanilla/pvchat/listeners/AsyncChatListener.java#L56).

No blocked word list is provided by default - you must define your own and place it at `plugins/pvFilter/blocked_words.txt`. Each word should occupy a new line. Lines begining with '\*' will be matched more strictly.
