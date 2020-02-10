
object Main extends App {
  val cliController:CliController = new CliController()
  println("Enter index or query commands. Press enter with an empty line to exit.")
  Iterator.continually(scala.io.StdIn.readLine())
    .takeWhile(line => line != null && line.nonEmpty)
    .foreach(line => {
      println(cliController.processInput(line))
    })
}
