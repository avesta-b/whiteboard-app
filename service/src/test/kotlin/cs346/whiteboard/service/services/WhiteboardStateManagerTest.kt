package cs346.whiteboard.service.services

// TODO: REVISIT THIS TEST CLASS WHEN WhiteBoardStateManager syncs up with DB
//@ExtendWith(MockitoExtension::class)
//class WhiteboardStateManagerTest {
//
//    @Mock
//    private lateinit var whiteboardTableRepository: WhiteboardTableRepository
//
//    @InjectMocks
//    private lateinit var whiteboardStateManager: WhiteboardStateManager
//
//    private val testRoomId: Long = 0
//
//    @BeforeEach
//    fun setup() {
//        // Setup initial WhiteboardState in the repository for testing
//        val whiteboardState = WhiteboardState()
//        val whiteboardTable = WhiteboardTable("foo", null, whiteboardState.toJsonString(), 0)
//        Mockito.`when`(whiteboardTableRepository.findByRoomId(testRoomId)).thenReturn(whiteboardTable)
//    }
//
//    @Test
//    fun `test getWhiteboard`() {
//        val whiteboardState = whiteboardStateManager.getWhiteboard(testRoomId)
//        assertNotNull(whiteboardState)
//        assertEquals(0, whiteboardState?.components?.size)
//
//    }
//
//    @Test
//    fun `test addComponent`() {
//        val component = ComponentState(/* Provide necessary values */)
//        whiteboardStateManager.addComponent(testRoomId, component)
//
//        val whiteboardState = whiteboardStateManager.getWhiteboard(testRoomId)
//        assertNotNull(whiteboardState)
//        if (whiteboardState != null) {
//            assertEquals(1, whiteboardState.components.size)
//        }
//        assertEquals(component, whiteboardState?.components?.get(component.uuid))
//
//    }
//
//    @Test
//    fun `test deleteComponent`() {
//        val component = ComponentState(/* Provide necessary values */)
//        whiteboardStateManager.addComponent(testRoomId, component)
//        whiteboardStateManager.deleteComponent(testRoomId, DeleteComponent(component.uuid))
//
//        val whiteboardState = whiteboardStateManager.getWhiteboard(testRoomId)
//        assertNotNull(whiteboardState)
//        assertEquals(0, whiteboardState?.components?.size)
//
//    }
//
//    @Test
//    fun `test updateComponent`() {
//        val component = ComponentState(/* Provide necessary values */)
//        whiteboardStateManager.addComponent(testRoomId, component)
//
//        val update = ComponentUpdate(uuid = component.uuid, size = Size(100f, 100f))
//        whiteboardStateManager.updateComponent(testRoomId, update)
//
//        val whiteboardState = whiteboardStateManager.getWhiteboard(testRoomId)
//        assertNotNull(whiteboardState)
//        val updatedComponent = whiteboardState?.components?.get(component.uuid)
//        assertNotNull(updatedComponent)
//
//        assertEquals(Size(100f, 100f), updatedComponent?.size)
//
//    }
//}
