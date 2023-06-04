package com.zzl.leetcode;

import java.util.Arrays;

/*给你一个由正整数组成的整数数组 nums ，返回其中可被 3 整除的所有偶数的平均值。
        注意：n 个元素的平均值等于 n 个元素 求和 再除以 n ，结果 向下取整 到最接近的整数。
        示例 1：

        输入：nums = [1,3,6,10,12,15]
        输出：9
        解释：6 和 12 是可以被 3 整除的偶数。(6 + 12) / 2 = 9 。
        示例 2：

        输入：nums = [1,2,4,7,10]
        输出：0
        解释：不存在满足题目要求的整数，所以返回 0 。*/
public class t2455可被三整除的偶数的平均值 {
    public static void main(String[] args) {
        int[] nums={1,3,6,10,12,15};
        System.out.println(averageValue(nums));
    }

    public static int averageValue(int[] nums) {
        //数组长度最大也不超过nums，存放数组中的偶数
        int []a=new int[nums.length];
        int count=0;
        for (int i = 0; i < nums.length; i++) {
            if(nums[i]%2==0&&nums[i]%3==0){
                a[count++]=nums[i];
            }
        }


        //数组流求和
        int sum = Arrays.stream(a).sum();

        return sum==0?0:(sum/count);
    }


//    示例代码
// 2455. Average Value of Even Numbers That Are Divisible by Three
    public int averageValue1(int[] nums) {
        int count = 0;
        int sum = 0;
        for (int num : nums) {
            if (num % 6 == 0) {
                ++count;
                sum += num;
            }
        }
        return count == 0 ? 0 : sum / count;
    }

}
