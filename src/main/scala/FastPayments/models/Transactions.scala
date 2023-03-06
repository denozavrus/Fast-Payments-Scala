package FastPayments.models
import java.util.UUID

case class ReplenishItem(id: UUID, amount: Int)
case class WithdrawItem(id: UUID, amount: Int)
case class TransferItem(from: UUID, to: UUID, amount: Int, category: CategoryObject)
case class TransferResponse(from: Account, to: Account)