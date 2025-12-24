package com.nbucs.studyroombackend.controller;

import com.nbucs.studyroombackend.dto.response.Response;
import com.nbucs.studyroombackend.entity.SeminarRoom;
import com.nbucs.studyroombackend.service.SeminarRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seminar-room")
public class SeminarRoomController {
    @Autowired
    private SeminarRoomService seminarRoomService;

    /**
     * 添加研讨室
     * @param seminarRoom 研讨室信息
     * @return 操作结果
     */
    @PostMapping("/add")
    public Response<?> addSeminarRoom(@RequestBody SeminarRoom seminarRoom) {
        System.out.println("添加研讨室请求：" + seminarRoom);

        try {
            seminarRoomService.addSeminarRoom(seminarRoom);
            return Response.success("添加成功", seminarRoom);
        } catch (Exception e) {
            System.err.println("添加研讨室失败: " + e.getMessage());
            return Response.error(304, "添加失败: " + e.getMessage());
        }
    }

    /**
     * 更新研讨室信息
     * @param seminarRoom 研讨室信息
     * @return 操作结果
     */
    @PutMapping("/update")
    public Response<?> updateSeminarRoom(@RequestBody SeminarRoom seminarRoom) {
        System.out.println("更新研讨室请求：" + seminarRoom);

        try {
            seminarRoomService.updateSeminarRoom(seminarRoom);
            return Response.success("更新成功", seminarRoom);
        } catch (Exception e) {
            System.err.println("更新研讨室失败: " + e.getMessage());
            return Response.error(304, "更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除研讨室
     * @param seminarRoom 研讨室信息
     * @return 操作结果
     */
    @DeleteMapping("/delete")
    public Response<?> deleteSeminarRoom(@RequestBody SeminarRoom seminarRoom) {
        System.out.println("删除研讨室请求：" + seminarRoom);

        try {
            boolean result = seminarRoomService.deleteSeminarRoom(seminarRoom);
            return result ? Response.success("删除成功", null) : Response.error(304, "删除失败");
        } catch (Exception e) {
            System.err.println("删除研讨室失败: " + e.getMessage());
            return Response.error(304, "删除失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有研讨室列表
     * @return 研讨室列表
     */
    @GetMapping("/all")
    public Response<?> getAllSeminarRooms() {
        System.out.println("获取所有研讨室请求");

        try {
            List<SeminarRoom> rooms = seminarRoomService.getAllSeminarRooms();
            return Response.success("查询成功", rooms);
        } catch (Exception e) {
            System.err.println("获取研讨室列表失败: " + e.getMessage());
            return Response.error(304, "查询失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取研讨室信息
     * @param id 研讨室ID
     * @return 研讨室信息
     */
    @GetMapping("/{id}")
    public Response<?> getSeminarRoomById(@PathVariable String id) {
        System.out.println("根据ID查询研讨室请求：" + id);

        try {
            SeminarRoom room = seminarRoomService.getSeminarRoomById(id);
            if (room == null) {
                return Response.error(404, "研讨室不存在");
            }
            return Response.success("查询成功", room);
        } catch (Exception e) {
            System.err.println("查询研讨室失败: " + e.getMessage());
            return Response.error(304, "查询失败: " + e.getMessage());
        }
    }
}
