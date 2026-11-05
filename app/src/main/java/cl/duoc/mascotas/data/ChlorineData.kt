package cl.duoc.mascotas.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "measurements")
data class Measurement(@PrimaryKey(autoGenerate = true) val id: Long = 0,
    val farm: String, val shed: String, val point: String, val chlorine: Double,
    val measuredAt: String, val status: String, val limits: String,
    val photoUri: String? = null, val source: String = "Control manual")

@Entity(tableName = "actions", foreignKeys = [ForeignKey(entity = Measurement::class,
    parentColumns = ["id"], childColumns = ["measurementId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("measurementId")])
data class CorrectiveAction(@PrimaryKey(autoGenerate = true) val id: Long = 0,
    val measurementId: Long, val description: String, val createdAt: String, val resolved: Boolean)

@Dao interface ChlorineDao {
    @Query("SELECT * FROM measurements ORDER BY measuredAt DESC, id DESC") fun observe(): Flow<List<Measurement>>
    @Query("SELECT * FROM actions ORDER BY id DESC") fun actions(): Flow<List<CorrectiveAction>>
    @Insert suspend fun insert(value: Measurement): Long
    @Insert suspend fun addAction(value: CorrectiveAction)
    @Query("DELETE FROM measurements WHERE id=:id") suspend fun delete(id: Long)
}
@Database(entities = [Measurement::class, CorrectiveAction::class], version = 1, exportSchema = true)
abstract class ChlorineDatabase : RoomDatabase() { abstract fun dao(): ChlorineDao }

data class MonitoringPoint(val farm: String, val shed: String, val name: String)
interface MonitoringApi { suspend fun points(): List<MonitoringPoint> }
/** Doble de API en memoria: datos ficticios, sin HTTP ni conexión empresarial. */
class SimulatedMonitoringApi : MonitoringApi {
    override suspend fun points() = listOf(MonitoringPoint("Granja Norte (demo)", "Galpón A", "Entrada de agua"),
        MonitoringPoint("Granja Norte (demo)", "Galpón B", "Bebederos"),
        MonitoringPoint("Granja Sur (demo)", "Galpón 1", "Estanque"))
}
class ChlorineRepository(private val dao: ChlorineDao, private val api: MonitoringApi) {
    val measurements = dao.observe()
    val actions = dao.actions()
    suspend fun points() = api.points()
    suspend fun save(value: Measurement) = dao.insert(value)
    suspend fun addAction(value: CorrectiveAction) = dao.addAction(value)
    suspend fun delete(id: Long) = dao.delete(id)
}
