package me.fru1t.subedit

import me.fru1t.subedit.utils.AssFile
import me.fru1t.subedit.utils.Utils


/** Monolith for the FirstLineSmaller tool. */
object FirstLineSmaller {
  private const val FONT_WRAPPER_BEFORE_TEMPLATE = "{\\fs%d}"
  private const val FONT_WRAPPER_AFTER = "{\\fs}"

  /**
   * Console application that adds a smaller font style to the first line of a multi-line subtitle. This method is
   * self-contained to accept input via CLI.
   *
   * Ignores lines that...
   *  - Are empty or single-line
   *  - Already have a style (even if it's not a font size style)
   */
  fun run() {
    println("RUNNING: First line smaller")

    println("Source file")
    val inFile = Utils.askForSourceFile() ?: return
    println("Target file")
    val outFile = Utils.askForOutputFile() ?: return
    println("Font size?")
    val fontSize = Utils.askForInt()

    AssFile.transformDialogue(inFile, outFile) { dialogue ->
      sequence {
        val text = dialogue.text
        val firstNewline = text.indexOf(Utils.SUB_NEWLINE)
        if (firstNewline == -1) {
          println("Ignoring single line")
          yield(dialogue)
          return@sequence
        }

        if (firstNewline > 0 && text[firstNewline - 1] == '}') {
          println("Ignored already fonted line")
          yield(dialogue)
          return@sequence
        }

        val firstLine = text.substring(0 until firstNewline)
        val rest = text.substring(firstNewline + Utils.SUB_NEWLINE.length)

        val newFirstLine = FONT_WRAPPER_BEFORE_TEMPLATE.format(fontSize) + firstLine + FONT_WRAPPER_AFTER
        yield(dialogue.copy(text = newFirstLine + Utils.SUB_NEWLINE + rest))
      }
    }

    println("Oki done. enjoy")
  }

}
