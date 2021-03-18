package me.haroldmartin.protobufdemo

import com.company.generated.model.services.roomservice.GetRoomDetailsResponseModel
import com.facebook.flipper.plugins.retrofit2protobuf.adapter.GenericCallDefinitionsToMessageDefinitionsIfProtobuf
import com.facebook.flipper.plugins.retrofit2protobuf.adapter.RetrofitServiceToGenericCallDefinitions
import me.haroldmartin.protobuf_java_to_protobufjs.adapter.FullNamedMessagesToNestedMessages
import tutorial.Dataformat.Person
import java.io.File

fun main(args: Array<String>) {
//    println(GeneratedMessageToReflectedTypes(GetRoomDetailsResponseModel::class.java))

    val person = Person.newBuilder().setId(2).setName("name").setEmail("email").setPhone("phone").build()
    File("person.proto").writeBytes(person.toByteArray())

//    val roomDetailsResponse = GetRoomDetailsResponseModel.newBuilder().set

//    println(GetRoomDetailsResponseModel.getDescriptor().fullName)
//    println(GetRoomDetailsResponseModel.getDescriptor().toProto().toString())
//    println(GetRoomDetailsResponseModel.getDescriptor().toProto().nestedTypeCount)

//    println(Person.getDescriptor().fullName)
//    println(Person.getDescriptor().toProto())

//    println(GetRoomDetailsResponseModel.getDescriptor().toProto())
    val callDefinitions = RetrofitServiceToGenericCallDefinitions(PersonService::class.java)
    val messageCallDefinitions = GenericCallDefinitionsToMessageDefinitionsIfProtobuf(callDefinitions)
    val nested = FullNamedMessagesToNestedMessages(messageCallDefinitions.find { it.path == "room.proto" }?.responseModel)
    println(nested)
}
