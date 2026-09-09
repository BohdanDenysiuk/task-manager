package com.bohdandenysiuk.taskmanager;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;


public class TaskManagerAppTest {

		@Test
		void removeTaskRemovesMiddleTask() {
			String[] values = {"A", "B", "C", null};
			String[] expected = {"A", "C", null, null};
			int testVal = TaskManagerApp.removeTask(values, 3, 1);
			
			assertEquals(2, testVal);
			assertArrayEquals(expected, values);
			
		}
		
		@Test
		void removeTaskRemovesFirstTask(){
			String[] values = {"A", "B", "C", null};
			String[] expected = {"B", "C", null, null};
			int testVal = TaskManagerApp.removeTask(values, 3, 0);
			
			assertEquals(2, testVal);
			assertArrayEquals(expected, values);
		}
		
		@Test
		void removeTaskRemovesLastTask() {
			String[] values = {"A", "B", "C", null};
			String[] expected = {"A", "B", null, null};
			int testVal = TaskManagerApp.removeTask(values, 3, 2);
			
			assertEquals(2, testVal);
			assertArrayEquals(expected, values);
		}
		
		@Test
		void removeTaskDoesNothingForInvalidIndex() {
			String[] values = {"A", "B", "C", null};
			String[] expected = values.clone();
			int testVal = TaskManagerApp.removeTask(values, 3, 3);
			
			assertEquals(3, testVal);
			assertArrayEquals(expected, values);
		}
		
		@Test
		void removeTaskDoesNothingForNegativeIndex() {
			String[] values = {"A", "B", "C", null};
			String[] expected = values.clone();
			int testVal = TaskManagerApp.removeTask(values, 3, -1);
			
			assertEquals(3, testVal);
			assertArrayEquals(expected, values);
		}
		
		@Test 
		void findTaskIndexFindsExistingTask() {
			String[] values = {"A", "B", "C", null};
			int testVal = TaskManagerApp.findTaskIndex(values, 3, "B");
			
			assertEquals(1, testVal);
		}
		
		@Test 
		void findTaskIndexReturnsMinusOneForMissingTask() {
			String[] values = {"A", "B", "C", null};
			int testVal = TaskManagerApp.findTaskIndex(values, 3, "D");
			
			assertEquals(-1, testVal);
		}
		
		@Test 
		void findTaskIndexReturnsMinusOneForNullArray() {
			String[] values = null;
			int testVal = TaskManagerApp.findTaskIndex(values, 0, "A");
			
			assertEquals(-1, testVal);
		}
		
		@Test 
		void findTaskIndexReturnsMinusOneForNullTitle() {
			String[] values = {"A", "B"};
			int testVal = TaskManagerApp.findTaskIndex(values, 2, null);
			
			assertEquals(-1, testVal);
		}
		
		@Test
		void findTaskIndexIgnoresElementsOutsideLogicalSize() {
			String[] values = {"A", "B", "hidden"};
			int testVal = TaskManagerApp.findTaskIndex(values, 2, "hidden");
			
			assertEquals(-1, testVal);
		}
		
		
		
		
		
		


}
