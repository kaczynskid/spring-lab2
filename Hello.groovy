@RestController
class Hello {

  @GetMapping('/hello')
  def hello() {
    return { message: 'Hello world!' }
  }
}
