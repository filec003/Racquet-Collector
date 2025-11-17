import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.racquetcollector.api.Racquet
import com.example.racquetcollector.R
class RacquetAdapter(private var racquets: List<Racquet>) :
    RecyclerView.Adapter<RacquetAdapter.RacquetViewHolder>() {

    class RacquetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val modelName: TextView = itemView.findViewById(R.id.modelName)
        val brandName: TextView = itemView.findViewById(R.id.brandName)
        val modelYear: TextView = itemView.findViewById(R.id.modelYear)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RacquetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.racquet_item, parent, false)
        return RacquetViewHolder(view)
    }

    override fun onBindViewHolder(holder: RacquetViewHolder, position: Int) {
        val racquet = racquets[position]
        holder.modelName.text = racquet.model_name
        holder.brandName.text = racquet.brand_name
        holder.modelYear.text = racquet.model_year.toString()
    }

    override fun getItemCount(): Int = racquets.size

    fun updateRacquets(newRacquets: List<Racquet>) {
        racquets = newRacquets
        notifyDataSetChanged()
    }
}
