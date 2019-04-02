package com.estsoft.web;

import java.util.List;

import javax.persistence.Column;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.estsoft.domain.Board;
import com.estsoft.repository.BoardRepository;

@Controller
@RequestMapping("/board")
@Transactional
public class BoardController {

	@Autowired
	private BoardRepository boardRepository;
	
	// ����Ʈ ��ȸ
	
	@GetMapping("/list")
	public String list() {
		return "/board/list";
	}
	
	@PostMapping("/getList")
	@ResponseBody 
	public List<Board> getList() {
	
		return boardRepository.findAllOrdering(); 
	}
	
	
	// ���
	
	@GetMapping("/write")
	public String write() {
		return "/board/write";
	}
	
	@PostMapping("/save")
	@ResponseBody 
	public Board save(@RequestBody Board board) {
		
		Board saveBoard = boardRepository.save(board);
		
		// ������ bNo�� groupNo ����
		saveBoard.setGroupNo(saveBoard.getNo());
		
		return boardRepository.save(saveBoard);
	}
	
	
	// ��
	
	@GetMapping("/detail/{bNo}")
	public String detail(@PathVariable int bNo, Model model) {
		
		model.addAttribute("board", boardRepository.findOne(bNo));
		
		return "/board/detail";
	}
	
	@PostMapping("/getBoard/{bNo}")
	@ResponseBody
	public Board getBoard(@PathVariable int bNo) {
		return boardRepository.findOne(bNo);
	}
	
	// ����
	
	@GetMapping("/modify/{bNo}")
	public String modify(@PathVariable int bNo, Model model) {
		
		model.addAttribute("board", boardRepository.findOne(bNo));
		
		return "/board/modify";
	}
	
	@PutMapping("/update/{bNo}")
	@ResponseBody
	public Board update(@PathVariable int bNo, @RequestBody Board board) {
		
		Board updateBoard = boardRepository.findOne(bNo);
		board.setNo(bNo);
		
		// ���� �� ���� ���� �ݿ�
		board.setGroupNo(updateBoard.getGroupNo());
		board.setGroupSeq(updateBoard.getGroupSeq());
		
		return boardRepository.save(board);
	}
	
	// ����
	
	@PutMapping("/delete/{bNo}")
	@ResponseBody
	public String delete(@PathVariable int bNo) {
		
		// ���� �����ʹ� �����ϵ�, delFlag = 'Y' ������Ʈ
		Board board = boardRepository.findOne(bNo);
		
		board.setDelFlag("Y");
		boardRepository.save(board);
		
		return "/board/list";
	}	
	
	// ��� ���
	
	@GetMapping("/write/{bNo}")
	public String writeReply(@PathVariable int bNo, Model model) {
		
		// ����� ����� ���
		// groupNo�� �ִ��� Ȯ��
		// groupNo = 0 : ����
		int groupNo = boardRepository.findGroupNoBybNo(bNo);
		
		// ������ ����� ���
		if(groupNo == 0) {
			groupNo = bNo;
		}
		
		model.addAttribute("groupNo", groupNo);
		model.addAttribute("parentNo", bNo);
		
		return "/board/reply";
	}
	
	@PostMapping("/saveReply")
	@ResponseBody 
	public Board saveReply(@RequestBody Board board) {
		
		// ���� ��ȣ�� groupSeq, parentNo, depth ����
		int groupNo = board.getGroupNo();
		int parentNo = board.getParentNo();
		
		// ������ ����� ���
		if(parentNo == 0) {
			parentNo = groupNo;
		}
		
		// groupSeq : ���� ���� ��ü ���� ����
		// parentNo : �θ� ��
		// depth : ���۷κ��� ���° ��������
		int depth = boardRepository.findDepthByParentNo(parentNo);
		int groupSeq = boardRepository.findMinGroupSeqByParentNoAndGroupNo(parentNo, groupNo);
		
		if(groupSeq > 0) {
			
			// ���� groupSeq �� �ڷ� �б�
			boardRepository.updateGroupSeq(groupNo, groupSeq);
						
			board.setGroupSeq(groupSeq);
			
		} else {
			
			groupSeq = boardRepository.findMaxGroupSeqByGroupNo(groupNo);			
			board.setGroupSeq(groupSeq + 1);
		
		}
		
		board.setDepth(depth + 1);
		
		return boardRepository.save(board);
	}

}
